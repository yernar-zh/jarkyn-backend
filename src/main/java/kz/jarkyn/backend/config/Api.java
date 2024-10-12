package kz.jarkyn.backend.config;

public interface Api {
    String PATH = "/api";

    interface Group {
        String PATH = Api.PATH + "/groups";
    }

    interface Attribute {
        String PATH = Api.PATH + "/attribute";
    }

    interface Good {
        String PATH = Api.PATH + "/goods";
    }
}
