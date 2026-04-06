package com.example.monitoringdemo1.controller;

import org.springframework.http.ResponseEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MemoryController {

    private final List<Object> processingQueue = new ArrayList<>();
    private final Counter bulkLoadCounter;

    public MemoryController(MeterRegistry registry) {
        this.bulkLoadCounter = Counter.builder("app_bulk_load_total")
                .description("Количество запусков массовой загрузки")
                .register(registry);
    }

    @GetMapping("/consume")
    public String bulkLoad(@RequestParam(defaultValue = "1") int batches) {
        bulkLoadCounter.increment();

        for (int i = 0; i < batches; i++) {
            byte[] dataChunk = new byte[100 * 1024 * 1024];
            processingQueue.add(dataChunk);
        }

        return "Загружено " + batches + " пакетов. Всего в памяти: " + (processingQueue.size() * 100) + " MB";
    }

    @GetMapping("/clear")
    public String flush() {
        processingQueue.clear();
        System.gc();
        return "Память освобождена (вызван GC)";
    }

    @GetMapping("/200") public String ok() {
        return "OK";
    }

    @GetMapping("/404") public ResponseEntity<String> nf() {
        return ResponseEntity.status(404).build();
    }

    @GetMapping("/500") public ResponseEntity<String> err() {
        return ResponseEntity.status(500).build();
    }
}
