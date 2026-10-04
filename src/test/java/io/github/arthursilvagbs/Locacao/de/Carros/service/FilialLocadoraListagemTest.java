package io.github.arthursilvagbs.Locacao.de.Carros.service;

import io.github.arthursilvagbs.Locacao.de.Carros.dto.filialLocadora.FilialLocadoraResponseDTO;
import io.github.arthursilvagbs.Locacao.de.Carros.entity.FilialLocadora;
import io.github.arthursilvagbs.Locacao.de.Carros.mapper.FilialLocadoraMapper;
import io.github.arthursilvagbs.Locacao.de.Carros.repository.FilialLocadoraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilialLocadoraListagemTest {
   @Mock FilialLocadoraRepository repository;
   @Mock FilialLocadoraMapper mapper;
   @InjectMocks FilialLocadoraService service;

   @Test
   void buscarTodosPaginado_mapeiaFiliaisOrdenadasPorNome() {
      FilialLocadora filial = mock(FilialLocadora.class);
      FilialLocadoraResponseDTO dto = mock(FilialLocadoraResponseDTO.class);
      when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(filial)));
      when(mapper.mapearParaResponse(filial)).thenReturn(dto);

      assertThat(service.buscarTodosPaginado().getContent()).containsExactly(dto);
      ArgumentCaptor<Pageable> pagina = ArgumentCaptor.forClass(Pageable.class);
      verify(repository).findAll(pagina.capture());
      assertThat(pagina.getValue().getPageNumber()).isZero();
      assertThat(pagina.getValue().getPageSize()).isEqualTo(10);
      assertThat(pagina.getValue().getSort().getOrderFor("nomeFilial").isAscending()).isTrue();
   }

   @Test
   void buscarTodosPaginado_semFiliais_retornaPaginaVazia() {
      when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());
      assertThat(service.buscarTodosPaginado()).isEmpty();
      verify(mapper, never()).mapearParaResponse(any());
   }
}
