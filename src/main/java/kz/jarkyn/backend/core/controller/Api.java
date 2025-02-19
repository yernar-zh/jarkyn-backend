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

    interface Organization {
        String PATH = Api.PATH + "/organization";
    }

    interface Warehouse {
        String PATH = Api.PATH + "/warehouse";
    }

    interface Account {
        String PATH = Api.PATH + "/account";
    }

    interface Supplier {
        String PATH = Api.PATH + "/supplier";
    }

    interface Customer {
        String PATH = Api.PATH + "/customer";
    }

    interface Supply {
        String PATH = Api.PATH + "/supply";
    }

    interface Sale {
        String PATH = Api.PATH + "/sale";
    }

    interface PaymentOut {
        String PATH = Api.PATH + "/payment_out";
    }

    interface PaymentIn {
        String PATH = Api.PATH + "/payment_in";
    }
}
