package com.applegrew.icemkr.record.field;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.IceRecordUtil;
import com.applegrew.icemkr.record.extension.ILookupFieldTypes;
import com.applegrew.icemkr.record.field.Field.FieldRequirementSpec;

public class FieldType {
    protected String name;

    protected String label;

    protected Class<? extends Field<?>> fieldClass;

    protected boolean isPrimary;

    protected FieldRequirementSpec fieldReqSpec;

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Class<? extends Field<?>> getFieldClass() {
        return fieldClass;
    }

    void setName(String n) {
        this.name = n;
        this.label = IceRecordUtil.nameToLabel(n);
    }

    void setFieldClass(Class<? extends Field<?>> fc) {
        checkNotNull(fc);
        this.fieldClass = fc;
        fieldReqSpec = Field.getFieldRequirements(fc);
        checkNotNull(fieldReqSpec);
    }

    public ScalarType getScalarType() {
        return fieldReqSpec.scalarType;
    }

    public int getMinLength() {
        return fieldReqSpec.minLength;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public static Builder createWithNameAndFieldClass(String n,
            Class<? extends Field<?>> fieldClass) {
        checkNotNull(n);
        checkNotNull(fieldClass);
        return new Builder(n, fieldClass);
    }

    public static class Builder {
        private FieldType f;

        private Builder(String n, Class<? extends Field<?>> fieldClass) {
            this.f = new FieldType();
            this.f.setName(n);
            this.f.setFieldClass(fieldClass);
        }

        public Builder withLabel(String lbl) {
            this.f.label = lbl;
            return this;
        }

        public Builder whichIsPrimary() {
            this.f.isPrimary = true;
            return this;
        }

        public FieldType build() {
            return this.f;
        }
    }

    public enum ScalarType {
        STRING, INTEGER, FLOAT, LARGE_TEXT, BINARY_DATA, BOOLEAN, TIMESTAMP;
        
//        public static WhereClauseParser.Value wrapInValueInstance(Object o) {
//            if (o instanceof List) {
//                WhereClauseParser.ArrayConst v = new WhereClauseParser.ArrayConst();
//                v.value
//            }
//        }
    }

    private static final List<FieldType> TYPES;
    static {
        TYPES = Arrays.asList(
                FieldType.createWithNameAndFieldClass(IceRecordConstants.CoreFieldTypeNames.STRING,
                        StringField.class).build(),
                FieldType.createWithNameAndFieldClass(IceRecordConstants.CoreFieldTypeNames.UUID,
                        UniqueIdField.class).build(),
                FieldType.createWithNameAndFieldClass(IceRecordConstants.CoreFieldTypeNames.CHOICE,
                        ChoiceField.class).build(),
                FieldType.createWithNameAndFieldClass(
                        IceRecordConstants.CoreFieldTypeNames.FIELD_TYPE_CHOICE,
                        FieldTypeChoiceField.class).build(),
                FieldType.createWithNameAndFieldClass(IceRecordConstants.CoreFieldTypeNames.PRIMARY,
                        UniqueIdField.class).whichIsPrimary().build(),
                FieldType
                        .createWithNameAndFieldClass(
                                IceRecordConstants.CoreFieldTypeNames.PRIMARY_STRING,
                                StringField.class)
                        .whichIsPrimary().build(),
                FieldType
                        .createWithNameAndFieldClass(
                                IceRecordConstants.CoreFieldTypeNames.REFERENCE,
                                ReferenceField.class)
                        .whichIsPrimary().build(),
                FieldType.createWithNameAndFieldClass(IceRecordConstants.CoreFieldTypeNames.INTEGER,
                        IntegerField.class).whichIsPrimary().build(),
                FieldType.createWithNameAndFieldClass(
                        IceRecordConstants.CoreFieldTypeNames.POSITIVE_INTEGER,
                        PositiveIntegerField.class).whichIsPrimary().build(),
                FieldType.createWithNameAndFieldClass(IceRecordConstants.CoreFieldTypeNames.BOOLEAN,
                        BooleanField.class).whichIsPrimary().build());
    }

    public static class StaticLookup implements ILookupFieldTypes {
        @Override
        public FieldType lookup(String typeName) {
            for (FieldType f : TYPES)
                if (f.name.equals(typeName))
                    return f;
            return null;
        }
    }

    public static FieldType lookup(String typeName) {
        return IceRecordEnv.getExtComponent().getLookupFieldTypes().lookup(typeName);
    }
}
