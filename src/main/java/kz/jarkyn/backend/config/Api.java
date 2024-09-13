package kz.jarkyn.backend.config;

public interface Api {
    String PATH = "api";

    interface Group {
        String PATH = Api.PATH + "/groups";
    }

    interface Transport {
        String PATH = Api.PATH + "/transports";
    }
}
