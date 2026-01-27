package kz.jarkyn.backend.good.controller;

import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.good.service.GoodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoodControllerTest {

    @Mock
    private GoodService goodService;

    @InjectMocks
    private GoodController goodController;

    @Test
    void list_shouldHandleMixedValueTypesInBody() {
        // Given
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("extra", "value");
        
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("name", List.of("1", "2", "3", "4"));
        bodyParams.put("$moment", "2026-01-24T10:25:20.445Z");
        bodyParams.put("page.size", 1000);

        when(goodService.findApiByFilter(any(), any(), any())).thenReturn(null);

        // When
        goodController.list(queryParams, bodyParams);

        // Then
        ArgumentCaptor<QueryParams> queryParamsCaptor = ArgumentCaptor.forClass(QueryParams.class);
        ArgumentCaptor<Instant> momentCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(goodService).findApiByFilter(queryParamsCaptor.capture(), any(), momentCaptor.capture());

        QueryParams capturedParams = queryParamsCaptor.getValue();
        assertThat(capturedParams.getPageSize()).isEqualTo(1000);
        assertThat(capturedParams.getFilters()).anySatisfy(filter -> {
            assertThat(filter.getName()).isEqualTo("name");
            assertThat(filter.getValues()).containsExactly("1", "2", "3", "4");
        });
        assertThat(capturedParams.getFilters()).anySatisfy(filter -> {
            assertThat(filter.getName()).isEqualTo("extra");
            assertThat(filter.getValues()).containsExactly("value");
        });
        assertThat(momentCaptor.getValue()).isEqualTo(Instant.parse("2026-01-24T10:25:20.445Z"));
    }
}
