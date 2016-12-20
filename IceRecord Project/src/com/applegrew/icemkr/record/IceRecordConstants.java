package com.applegrew.icemkr.record;

public interface IceRecordConstants {

    public interface FieldNames {
        String SYS_ID = "sys_id";

        String SYS_MOD_COUNT = "sys_mod_count";

        String SYS_CREATED_BY = "sys_created_by";

        String SYS_CREATED_ON = "sys_created_on";

        String SYS_UPDATED_BY = "sys_updated_by";

        String SYS_UPDATED_ON = "sys_updated_on";

        String NAME = "name";

        String LABEL = "label";

        String EXTENDS = "extends";

        String TYPE = "type";

        String MAX_LENGTH = "max_length";

        String READONLY = "readonly";

        String MANDATORY = "mandatory";

        String DISPLAY = "display";

        String DEFAULT_VALUE = "default_value";

        String COLLECTION = "collection";

        String UNIQUE = "unique_";
    }

    int UNIQUEID_LENGTH = 36;

    String EN_LANG = "en";

    public interface CoreFieldTypeNames {
        String STRING = "string";

        String UUID = "uuid";

        String PRIMARY = "primary";

        String PRIMARY_STRING = "primary_string";

        String CHOICE = "choice";

        String FIELD_TYPE_CHOICE = "field_type_choice";

        String REFERENCE = "reference";

        String INTEGER = "integer";

        String POSITIVE_INTEGER = "positive_integer";

        String BOOLEAN = "boolean";

        String TABLE_CHOICE = "table_choice";
    }

    public interface CoreTableNames {
        String SYS_COLLECTION = "sys_collection";

        String SYS_DICTIONARY = "sys_dictionary";
    }

    public interface Choices {
        String COLLECTION = "collection";

        String FIELD = "field";
    }
}
