package kz.jarkyn.backend.service;


import kz.jarkyn.backend.model.good.TransportEntity;
import kz.jarkyn.backend.model.good.api.TransportCreateApi;
import kz.jarkyn.backend.model.good.api.TransportDetailApi;
import kz.jarkyn.backend.model.good.api.TransportEditApi;
import kz.jarkyn.backend.model.good.api.TransportListApi;
import kz.jarkyn.backend.repository.TransportRepository;
import kz.jarkyn.backend.service.mapper.TransportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransportService {
    private final TransportRepository transportRepository;
    private final TransportMapper transportMapper;

    public TransportService(TransportRepository transportRepository, TransportMapper transportMapper) {
        this.transportRepository = transportRepository;
        this.transportMapper = transportMapper;
    }

    @Transactional(readOnly = true)
    public List<TransportListApi> findApiBy() {
        List<TransportEntity> entities = transportRepository.findAll();
        // Create a map of parent transports to their children using transportingBy
        Map<TransportEntity, List<TransportEntity>> childrenMap = entities.stream()
                .filter(transport -> transport.getParent() != null) // Filter out transports without parents
                .collect(Collectors.groupingBy(TransportEntity::getParent,
                        Collectors.collectingAndThen(
                                Collectors.toList(), list -> {
                                    list.sort(Comparator.comparing(TransportEntity::getPosition)); // Sort by position
                                    return list;
                                }
                        )
                ));
        // Find all head transports (transports without a parent) using a separate stream operation
        List<TransportEntity> heads = entities.stream().filter(transport -> transport.getParent() == null).toList();
        // Convert head transports into API model objects, using the childrenMap for hierarchy
        return heads.stream().map(head -> transportMapper.toListApi(head, childrenMap)).collect(Collectors.toList());
    }

    @Transactional
    public TransportDetailApi createApi(TransportCreateApi createApi) {
        TransportEntity entity = transportMapper.toEntity(createApi);
        return transportMapper.toDetailApi(transportRepository.save(entity));
    }

    @Transactional
    public TransportDetailApi editApi(UUID id, TransportEditApi editApi) {
        TransportEntity entity = transportRepository.findById(id).orElseThrow();
        transportMapper.editEntity(entity, editApi);
        return transportMapper.toDetailApi(transportRepository.save(entity));
    }
}
