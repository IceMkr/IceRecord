package com.applegrew.icemkr.record.field;

import static com.google.common.base.Preconditions.checkNotNull;

import com.applegrew.icemkr.record.IceRecordUtil;

public class FieldMeta {

    protected FieldType fieldType;

    protected String fieldName;

    protected String fieldLabel;

    protected int maxLength;

    protected boolean isReadonly;

    protected boolean isMandatory;

    protected boolean isDisplay;

    protected boolean isUnique;

    protected String defaultValueExpression;

    protected Field<?> fieldInstance;

    // protected List<Choice> choices;

    public FieldMeta(String fieldName) {
        this.fieldName = fieldName;
        this.fieldLabel = IceRecordUtil.nameToLabel(fieldName);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDefaultValueExpression() {
        return defaultValueExpression;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public int getMaxLength() {
        return maxLength;
    }

    boolean setMaxLength(int maxLen) {
        if (maxLen >= fieldType.getMinLength()) {
            this.maxLength = maxLen;
            return true;
        }
        return false;
    }

    public boolean isReadonly() {
        return isReadonly;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public boolean isDisplay() {
        return isDisplay;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public Field<?> getFieldInstance() {
        if (fieldInstance != null)
            return fieldInstance;
        try {
            fieldInstance = this.fieldType.fieldClass.newInstance();
            fieldInstance.setFieldMeta(this);
            return fieldInstance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Builder createWithFieldNameAndFieldType(String n, String fieldTypeName) {
        FieldType ft = FieldType.lookup(fieldTypeName);
        checkNotNull(ft);
        return new Builder(n, ft);
    }

    public static class Builder {
        private FieldMeta f;

        private Builder(String n, FieldType ft) {
            this.f = new FieldMeta(n);
            this.f.fieldType = ft;
        }

        public Builder withFieldLabel(String lbl) {
            this.f.fieldLabel = lbl;
            return this;
        }

        public Builder withMaxLength(int len) {
            this.f.setMaxLength(len);
            return this;
        }

        public Builder withDefaultExpression(String exp) {
            this.f.defaultValueExpression = exp;
            return this;
        }

        public Builder whichIsReadonly() {
            this.f.isReadonly = true;
            return this;
        }

        public Builder whichIsMandatory() {
            this.f.isMandatory = true;
            return this;
        }

        public Builder whichIsUnique() {
            this.f.isUnique = true;
            return this;
        }

        public Builder whichIsDisplayField() {
            this.f.isDisplay = true;
            return this;
        }

        // public Builder withChoices(Choice... choices) {
        // checkNotNull(choices);
        // if (this.f.choices == null) {
        // this.f.choices = new ArrayList<>();
        // }
        // this.f.choices.addAll(Lists.newArrayList(choices));
        // return this;
        // }

        public FieldMeta build() {
            if (this.f.maxLength == 0) {
                if (f.fieldType.getMinLength() > 0)
                    this.f.maxLength = f.fieldType.getMinLength();
                else
                    this.f.maxLength = 200;
            }
            return this.f;
        }
    }
}
