package com.master.air.controller;

import com.master.air.model.Annonce;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meta")
@Tag(name = "Meta / Introspection", description = "Metadata API via reflexion")
public class MetaController {

    private static final Set<String> EXCLUDED = Set.of("version", "author", "category");

    @GetMapping("/annonces")
    @Operation(summary = "Champs filtrables/triables/cherchables via introspection")
    public ResponseEntity<Map<String, Object>> getAnnonceMetadata() {
        Field[] fields = Annonce.class.getDeclaredFields();

        List<String> sortable = Arrays.stream(fields).map(Field::getName)
                .filter(n -> !EXCLUDED.contains(n)).collect(Collectors.toList());

        List<String> searchable = Arrays.stream(fields)
                .filter(f -> f.getType() == String.class)
                .map(Field::getName).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "entity", "Annonce",
                "sortableFields", sortable,
                "filterableParams", List.of("q","status","categoryId","authorId","fromDate","toDate"),
                "searchableTextFields", searchable
        ));
    }
}
