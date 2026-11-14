package io.github.kypleefearz.bodybuilding.common;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, String id) {
        super("%s '%s' was not found".formatted(resource, id));
    }
}
