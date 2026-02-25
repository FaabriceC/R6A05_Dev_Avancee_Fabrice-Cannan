package com.master.air.controller;

import com.master.air.model.Annonce;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("/api/meta")
public class AnnonceMetaController {

    @GetMapping("/annonces")
    public Map<String, Object> annoncesMeta() {

        Set<String> blocked = Set.of("author", "category", "version");

        List<String> sortable = new ArrayList<>();
        for (Field f : Annonce.class.getDeclaredFields()) {
            String name = f.getName();
            if (blocked.contains(name)) continue;
            sortable.add(name);
        }
        Collections.sort(sortable);

        List<String> filterable = List.of("q", "status", "categoryId", "authorId", "fromDate", "toDate");

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("sortable", sortable);
        resp.put("filterable", filterable);
        return resp;
    }
}