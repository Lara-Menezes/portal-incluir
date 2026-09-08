package br.com.portalincluir.controller;

import br.com.portalincluir.dto.request.CoordenadorRequest;
import br.com.portalincluir.dto.response.CoordenadorResponse;
import br.com.portalincluir.enums.StatusCoordenador;
import br.com.portalincluir.service.CoordenadorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordenadores")
public class CoordenadorController {

    private final CoordenadorService coordenadorService;

    public CoordenadorController(CoordenadorService coordenadorService) {
        this.coordenadorService = coordenadorService;
    }

    // Criar
    @PostMapping
    public CoordenadorResponse criar(@Valid @RequestBody CoordenadorRequest request) {
        return coordenadorService.criar(request);
    }

    // Listar
    @GetMapping
    public List<CoordenadorResponse> listar(
            @RequestParam(required = false) StatusCoordenador status) {

        if (status != null) {
            return coordenadorService.listarPorStatus(status);
        }

        return coordenadorService.listarTodos();
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public CoordenadorResponse buscarPorId(@PathVariable Long id) {
        return coordenadorService.buscarPorId(id);
    }

    // Atualizar
    @PutMapping("/{id}")
    public CoordenadorResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CoordenadorRequest request) {

        return coordenadorService.atualizar(id, request);
    }

    // Inativar
    @PostMapping("/{id}/inativar")
    public CoordenadorResponse inativar(@PathVariable Long id) {
        return coordenadorService.inativar(id);
    }

    // Reativar
    @PostMapping("/{id}/reativar")
    public CoordenadorResponse reativar(@PathVariable Long id) {
        return coordenadorService.reativar(id);
    }
}
