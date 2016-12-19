package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;

@FieldRequirements(scalarType = ScalarType.STRING)
public class ChoiceField extends StringField {

    // TODO add provision to store choices

    public static class Choice {
        protected String label;

        protected String value;

        protected String language;

        public Choice(String lbl, String val) {
            this(lbl, val, IceRecordConstants.EN_LANG);
        }

        public Choice(String lbl, String val, String lang) {
            label = lbl;
            value = val;
            language = lang;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

    }

}
