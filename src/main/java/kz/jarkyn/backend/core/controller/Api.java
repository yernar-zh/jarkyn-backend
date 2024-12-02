package kz.jarkyn.backend.core.controller;

public interface Api {
    String PATH = "/api";

    interface Group {
        String PATH = Api.PATH + "/group";
    }

    interface AttributeGroup {
        String PATH = Api.PATH + "/attribute_group";
    }

    interface Attribute {
        String PATH = Api.PATH + "/attribute";
    }

    interface Good {
        String PATH = Api.PATH + "/good";
    }

    interface Warehouse {
        String PATH = Api.PATH + "/warehouse";
    }

    interface Account {
        String PATH = Api.PATH + "/account";
    }

    interface Customer {
        String PATH = Api.PATH + "/customer";
    }

    interface Sale {
        String PATH = Api.PATH + "/sale";
    }
}
