package com.phcworld.boardanswerservice.mock;

import com.phcworld.boardanswerservice.controller.AnswerQueryApiController;
import com.phcworld.boardanswerservice.controller.AnswerCommandApiController;
import com.phcworld.boardanswerservice.controller.port.AnswerService;
import com.phcworld.boardanswerservice.controller.port.WebClientService;
import com.phcworld.boardanswerservice.service.AnswerServiceImpl;
import com.phcworld.boardanswerservice.service.port.*;
import lombok.Builder;

public class TestContainer {

    public final AnswerRepository answerRepository;
    public final BoardProducer boardProducer;
    public final AnswerProducer answerProducer;
    public final AnswerService answerService;
    public final AnswerQueryApiController answerQueryApiController;
    public final WebClientService webClientService;

    public final AnswerCommandApiController answerCommandApiController;

    @Builder
    public TestContainer(LocalDateTimeHolder localDateTimeHolder, UuidHolder uuidHolder){
        this.answerRepository = new FakeAnswerRepository();
        this.boardProducer = new FakeBoardProducer();
        this.answerProducer = new FakeAnswerProducer();
        this.webClientService = new FakeWebClientService();
        this.answerService = AnswerServiceImpl.builder()
                .timeHolder(localDateTimeHolder)
                .uuidHolder(uuidHolder)
                .boardProducer(boardProducer)
                .answerProducer(answerProducer)
                .answerRepository(answerRepository)
                .build();
        this.answerCommandApiController = AnswerCommandApiController.builder()
                .answerService(answerService)
                .webClientService(webClientService)
                .build();
        this.answerQueryApiController = AnswerQueryApiController.builder()
                .answerService(answerService)
                .webClientService(webClientService)
                .build();
    }

}
