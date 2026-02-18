package com.master.air.rest;

import com.master.air.dto.*;
import com.master.air.exception.ConflictException;
import com.master.air.exception.ForbiddenException;
import com.master.air.exception.NotFoundException;
import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import com.master.air.model.PaginatedResult;
import com.master.air.service.AnnonceService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;


@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private static final Logger log = LoggerFactory.getLogger(AnnonceResource.class);
    private final AnnonceService annonceService = new AnnonceService();

    @GET
    public Response list(@QueryParam("page") @DefaultValue("1") int page,
                         @QueryParam("size") @DefaultValue("6") int size,
                         @QueryParam("keyword") String keyword,
                         @QueryParam("categoryId") Long categoryId,
                         @QueryParam("status") String status) {

        PaginatedResult<Annonce> result;

        if (keyword != null && !keyword.isBlank()) {
            result = annonceService.searchAnnonces(keyword.trim(), page, size);
        } else if (categoryId != null || status != null) {
            AnnonceStatus st = null;
            if (status != null && !status.isBlank()) {
                try { st = AnnonceStatus.valueOf(status); }
                catch (IllegalArgumentException e) {
                    return Response.status(400)
                            .entity(ErrorResponse.builder().status(400).error("Bad Request")
                                    .message("Statut invalide: " + status + ". Valeurs possibles: DRAFT, PUBLISHED, ARCHIVED").build())
                            .build();
                }
            }
            result = annonceService.filterAnnonces(categoryId, st, page, size);
        } else {
            result = annonceService.listAnnonces(page, size);
        }

        List<AnnonceDTO> dtos = result.getItems().stream().map(AnnonceDTO::fromEntity).toList();
        PaginatedResponse<AnnonceDTO> response = new PaginatedResponse<>(dtos, result.getTotalCount(), result.getPage(), result.getPageSize());

        return Response.ok(response).build();
    }


    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Annonce annonce = annonceService.getAnnonce(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable (id=" + id + ")"));
        return Response.ok(AnnonceDTO.fromEntity(annonce)).build();
    }


    @POST
    public Response create(@Valid AnnonceDTO dto, @Context ContainerRequestContext ctx) {
        Long userId = (Long) ctx.getProperty("userId");
        Annonce annonce = annonceService.createAnnonce(
                dto.getTitle(), dto.getDescription(), dto.getAdress(),
                dto.getMail(), userId, dto.getCategoryId());

        AnnonceDTO created = AnnonceDTO.fromEntity(annonce);
        return Response.created(URI.create("/api/annonces/" + created.getId()))
                .entity(created)
                .build();
    }


    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid AnnonceDTO dto,
                           @Context ContainerRequestContext ctx) {
        Long userId = (Long) ctx.getProperty("userId");
        Annonce annonce = annonceService.updateAnnonce(id,
                dto.getTitle(), dto.getDescription(), dto.getAdress(),
                dto.getMail(), dto.getCategoryId(), userId);
        return Response.ok(AnnonceDTO.fromEntity(annonce)).build();
    }


    @PATCH
    @Path("/{id}")
    public Response patch(@PathParam("id") Long id, AnnoncePatchDTO dto,
                          @Context ContainerRequestContext ctx) {
        Long userId = (Long) ctx.getProperty("userId");

        if (dto.getStatus() != null) {
            try {
                AnnonceStatus newStatus = AnnonceStatus.valueOf(dto.getStatus());
                switch (newStatus) {
                    case PUBLISHED -> annonceService.publishAnnonce(id);
                    case ARCHIVED -> annonceService.archiveAnnonce(id);
                    default -> throw new ConflictException("Transition vers DRAFT non autorisee");
                }
            } catch (IllegalArgumentException e) {
                return Response.status(400)
                        .entity(ErrorResponse.builder().status(400).error("Bad Request")
                                .message("Statut invalide: " + dto.getStatus()).build())
                        .build();
            }
        }


        Annonce current = annonceService.getAnnonce(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable"));

        String title = dto.getTitle() != null ? dto.getTitle() : current.getTitle();
        String desc = dto.getDescription() != null ? dto.getDescription() : current.getDescription();
        String adress = dto.getAdress() != null ? dto.getAdress() : current.getAdress();
        String mail = dto.getMail() != null ? dto.getMail() : current.getMail();
        Long catId = dto.getCategoryId() != null ? dto.getCategoryId() : current.getCategory().getId();


        if (dto.getTitle() != null || dto.getDescription() != null || dto.getAdress() != null
                || dto.getMail() != null || dto.getCategoryId() != null) {
            Annonce updated = annonceService.updateAnnonce(id, title, desc, adress, mail, catId, userId);
            return Response.ok(AnnonceDTO.fromEntity(updated)).build();
        }


        Annonce result = annonceService.getAnnonce(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable"));
        return Response.ok(AnnonceDTO.fromEntity(result)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context ContainerRequestContext ctx) {
        Long userId = (Long) ctx.getProperty("userId");
        annonceService.deleteAnnonce(id, userId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/publish")
    public Response publish(@PathParam("id") Long id) {
        Annonce annonce = annonceService.publishAnnonce(id);
        return Response.ok(AnnonceDTO.fromEntity(annonce)).build();
    }

    @POST
    @Path("/{id}/archive")
    public Response archive(@PathParam("id") Long id) {
        Annonce annonce = annonceService.archiveAnnonce(id);
        return Response.ok(AnnonceDTO.fromEntity(annonce)).build();
    }
}
