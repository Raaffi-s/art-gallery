package com.gallery.catalog.controller;

import com.gallery.catalog.dto.PaintingDto;
import com.gallery.catalog.service.PaintingService;
import com.gallery.catalog.service.TransactionDemoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final PaintingService paintingService;
    private final TransactionDemoService transactionDemoService;

    public DemoController(PaintingService paintingService,
                          TransactionDemoService transactionDemoService) {
        this.paintingService = paintingService;
        this.transactionDemoService = transactionDemoService;
    }

    /**
     * Демонстрация проблемы N+1.
     *
     * Используется paintingRepository.findAll() — Hibernate загружает список картин
     * одним SELECT, а затем для каждой картины отдельно выполняет SELECT для получения
     * связанных user, gallery и tags. Итого: 1 + N*3 запросов к БД.
     *
     * Смотри SQL-запросы в логах: logging.level.org.hibernate.SQL=DEBUG
     */
    @GetMapping("/n-plus-1")
    public ResponseEntity<List<PaintingDto>> demoNplus1() {
        List<PaintingDto> paintings = paintingService.getPaintingsWithNplus1Problem();
        return ResponseEntity.ok(paintings);
    }

    /**
     * Демонстрация решения проблемы N+1 через @EntityGraph.
     *
     * Используется findAllByOrderByCreatedAtDesc(), аннотированный @EntityGraph
     * с attributePaths = {"user", "gallery", "tags"}.
     * Hibernate генерирует один LEFT JOIN запрос, подтягивая все связи сразу.
     * Итого: всего 1 запрос к БД вместо 1 + N*3.
     *
     * Сравни количество SQL-запросов в логах с эндпоинтом /n-plus-1.
     */
    @GetMapping("/n-plus-1-solved")
    public ResponseEntity<List<PaintingDto>> demoNplus1Solved() {
        List<PaintingDto> paintings = paintingService.getAllPaintings();
        return ResponseEntity.ok(paintings);
    }

    /**
     * Демонстрация частичного сохранения без @Transactional.
     *
     * Метод последовательно сохраняет User, Gallery и Painting через отдельные
     * вызовы repository.save(). Каждый save() фиксируется немедленно в БД
     * (auto-commit). При возникновении RuntimeException после сохранения Painting
     * все три записи остаются в базе — частичное сохранение состоялось.
     *
     * Проверь через GET /api/users — новый пользователь будет в базе.
     */
    @GetMapping("/without-transaction")
    public ResponseEntity<String> demoWithoutTransaction() {
        try {
            transactionDemoService.createGalleryWithoutTransaction();
            return ResponseEntity.ok("Operation completed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage()
                    + " — Data partially saved in database!"
                    + " Check GET /api/users to confirm.");
        }
    }

    /**
     * Демонстрация полного отката с @Transactional.
     *
     * Метод помечен @Transactional(rollbackFor = Exception.class).
     * Все три save() выполняются в рамках одной транзакции — данные не фиксируются
     * до её завершения. При RuntimeException Spring автоматически вызывает rollback,
     * и ни одна запись не попадает в БД.
     *
     * Примечание: rollbackFor = Exception.class здесь избыточен, так как
     *
     * @Transactional по умолчанию откатывается на любой RuntimeException.
     * Указан явно в учебных целях для демонстрации параметра.
     *
     * Проверь через GET /api/users — нового пользователя не будет в базе.
     */
    @GetMapping("/with-transaction")
    public ResponseEntity<String> demoWithTransaction() {
        try {
            transactionDemoService.createGalleryWithTransaction();
            return ResponseEntity.ok("Operation completed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage()
                    + " — Data fully rolled back!"
                    + " Check GET /api/users to confirm.");
        }
    }
}

