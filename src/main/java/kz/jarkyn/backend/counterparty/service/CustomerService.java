
package kz.jarkyn.backend.counterparty.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.AccountEntity_;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity_;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
import kz.jarkyn.backend.counterparty.repository.CustomerRepository;
import kz.jarkyn.backend.counterparty.service.mapper.CustomerMapper;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.SaleEntity_;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditService auditService;
    private final EntityManager entityManager;
    private final ConversionService conversionService;


    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            AuditService auditService,
            EntityManager entityManager,
            ConversionService conversionService
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditService = auditService;
        this.entityManager = entityManager;
        this.conversionService = conversionService;
    }

    @Transactional(readOnly = true)
    public CustomerResponse findApiById(UUID id) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return customerMapper.toApi(customer);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> findApiByFilter(QueryParams requestQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerResponse> query = cb.createQuery(CustomerResponse.class);

        Root<CustomerEntity> customerRoot = query.from(CustomerEntity.class);
        ListJoin<CustomerEntity, AccountEntity> accountJoin = customerRoot.join(CustomerEntity_.accounts, JoinType.LEFT);
        ListJoin<CustomerEntity, SaleEntity> saleJoin = cb.treat(
                customerRoot.join(CustomerEntity_.documents, JoinType.LEFT), SaleEntity.class);

        Map<String, Expression<?>> attributes = new LinkedHashMap<>();
        attributes.put("id", customerRoot.get(CustomerEntity_.id));
        attributes.put("name", customerRoot.get(CustomerEntity_.name));
        attributes.put("phoneNumber", customerRoot.get(CustomerEntity_.phoneNumber));
        attributes.put("shippingAddress", customerRoot.get(CustomerEntity_.shippingAddress));
        attributes.put("discount", customerRoot.get(CustomerEntity_.discount));
        attributes.put("balance", accountJoin.get(AccountEntity_.balance));
        attributes.put("firstSale", cb.least(saleJoin.get(SaleEntity_.moment)));
        attributes.put("lastSale", cb.greatest(saleJoin.get(SaleEntity_.moment)));
        attributes.put("totalSaleCount", cb.count(saleJoin).as(Integer.class));
        attributes.put("totalSaleAmount", cb.sum(saleJoin.get(SaleEntity_.amount)));
        Selection<?>[] selectionArray = attributes.values().toArray(Selection<?>[]::new);

        List<Expression<?>> searchAttributes = new ArrayList<>();
        searchAttributes.add(attributes.get("name"));
        searchAttributes.add(attributes.get("phoneNumber"));
        searchAttributes.add(attributes.get("shippingAddress"));

        List<Expression<?>> groupByAttributes = new ArrayList<>();
        groupByAttributes.add(attributes.get("id"));
        groupByAttributes.add(attributes.get("name"));
        groupByAttributes.add(attributes.get("phoneNumber"));
        groupByAttributes.add(attributes.get("shippingAddress"));
        groupByAttributes.add(attributes.get("discount"));
        groupByAttributes.add(attributes.get("balance"));

        Predicate staticPredicate = cb.equal(saleJoin.get(SaleEntity_.state), SaleState.SHIPPED);
        Predicate searchPredicate = searchToPredicate(cb, requestQuery.getSearch(), searchAttributes);
        Predicate wherePredicate = filterToWherePredicate(cb, requestQuery.getFilters(), attributes);
        Predicate havingPredicate = filterToHavingPredicate(cb, requestQuery.getFilters(), attributes);

        List<Order> orderList = sortToOrder(cb, requestQuery.getSorts(), attributes);

        query.select(cb.construct(CustomerResponse.class, selectionArray));
        query.where(cb.and(staticPredicate, searchPredicate, wherePredicate));
        query.groupBy(groupByAttributes);
        query.having(havingPredicate);
        query.orderBy(orderList);
        List<CustomerResponse> rows = entityManager.createQuery(query).getResultList();
        return null;
    }

    private Predicate searchToPredicate(
            CriteriaBuilder cb,
            String search,
            List<Expression<?>> attributes) {
        Expression<?>[] expressions = new Expression<?>[attributes.size() + 1];
        expressions[0] = cb.literal(search);
        for (int i = 1; i <= attributes.size(); i++) {
            expressions[i] = attributes.get(i - 1);
        }
        return cb.isTrue(cb.function("search", Boolean.class, expressions));
    }

    private Predicate filterToWherePredicate(
            CriteriaBuilder cb,
            List<QueryParams.Filter> filters,
            Map<String, Expression<?>> attributes) {
        return filters.stream()
                .filter(filter -> attributes.containsKey(filter.getName()))
                .filter(filter -> filter instanceof Path<?>)
                .map(filter -> {
                    Expression<Comparable> expression = (Expression<Comparable>) attributes.get(filter.getName());
                    Comparable value = conversionService.convert(filter.getValue(), expression.getJavaType());
                    return switch (filter.getType()) {
                        case EQUAL_TO -> cb.equal(expression, value);
                        case LESS_THEN -> cb.lessThanOrEqualTo(expression, value);
                        case GREATER_THEN -> cb.greaterThanOrEqualTo(expression, value);
                    };
                })
                .reduce(cb::and).orElse(cb.conjunction());
    }

    private Predicate filterToHavingPredicate(
            CriteriaBuilder cb,
            List<QueryParams.Filter> filters,
            Map<String, Expression<?>> attributes) {
        return filters.stream()
                .filter(filter -> attributes.containsKey(filter.getName()))
                .filter(filter -> !(filter instanceof Path<?>))
                .map(filter -> {
                    Expression<Comparable> expression = (Expression<Comparable>) attributes.get(filter.getName());
                    Comparable value = conversionService.convert(filter.getValue(), expression.getJavaType());
                    return switch (filter.getType()) {
                        case EQUAL_TO -> cb.equal(expression, value);
                        case LESS_THEN -> cb.lessThanOrEqualTo(expression, value);
                        case GREATER_THEN -> cb.greaterThanOrEqualTo(expression, value);
                    };
                })
                .reduce(cb::and).orElse(cb.conjunction());
    }

    private List<Order> sortToOrder(
            CriteriaBuilder cb,
            List<QueryParams.Sort> sorts,
            Map<String, Expression<?>> attributes) {
        return sorts.stream()
                .filter(filter -> attributes.containsKey(filter.getName()))
                .map(filter -> {
                    Expression<?> expression = attributes.get(filter.getName());
                    return switch (filter.getType()) {
                        case ASC -> cb.asc(expression);
                        case DESC -> cb.desc(expression);
                    };
                }).toList();
    }

    @Transactional
    public UUID createApi(CustomerRequest request) {
        CustomerEntity customer = customerRepository.save(customerMapper.toEntity(request));
        auditService.saveChanges(customer);
        return customer.getId();
    }

    @Transactional
    public void editApi(UUID id, CustomerRequest request) {
        CustomerEntity customer = customerRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        customerMapper.editEntity(customer, request);
        auditService.saveChanges(customer);
    }
}
