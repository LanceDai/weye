package cn.lancedai.weye.server.aop;

import cn.lancedai.weye.common.exception.SQLExecuteErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> exceptionHandler(Throwable throwable) {
//        throwable.printStackTrace();
        log.error("aop=> throwableMessage: {}", throwable.getMessage());
        if (throwable instanceof SQLExecuteErrorException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("server work fail");
        } else {
//            throwable.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(throwable.getMessage());
        }
    }
}
