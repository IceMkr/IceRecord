package com.applegrew.icemkr.record.dialect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.dialect.Select.WhereClause;
import com.applegrew.icemkr.record.dialect.Table.TableMeta;
import com.applegrew.icemkr.record.extension.IConnectionManager;
import com.applegrew.icemkr.record.field.Field;
import com.applegrew.icemkr.record.field.FieldMeta;
import com.applegrew.icemkr.record.field.FieldType;
import com.applegrew.icemkr.record.javacc.parser.ParseException;
import com.applegrew.icemkr.record.javacc.parser.WhereClauseParser;
import com.applegrew.icemkr.record.javacc.parser.WhereClauseParser.FieldExpression;
import com.applegrew.icemkr.record.javacc.parser.WhereClauseParser.ParsedExpression;
import com.applegrew.icemkr.record.javacc.parser.WhereClauseParser.SubQuery;

public class TableHandler {
    private final static TableMeta SYS_COLLECTION_META;

    private final static TableMeta SYS_DICTIONARY_META;
    static {
        SYS_COLLECTION_META = new TableMeta();
        SYS_COLLECTION_META.setTableName(IceRecordConstants.CoreTableNames.SYS_COLLECTION);
        SYS_COLLECTION_META.addUniqueFieldsMeta(new FieldMeta[] {
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.SYS_ID,
                        IceRecordConstants.CoreFieldTypeNames.PRIMARY).build(),
                FieldMeta
                        .createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.NAME,
                                IceRecordConstants.CoreFieldTypeNames.STRING)
                        .whichIsMandatory().whichIsUnique().build(),
                FieldMeta
                        .createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.LABEL,
                                IceRecordConstants.CoreFieldTypeNames.STRING)
                        .whichIsMandatory().build(),
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.EXTENDS,
                        IceRecordConstants.CoreFieldTypeNames.REFERENCE).build(), });

        SYS_DICTIONARY_META = new TableMeta();
        SYS_DICTIONARY_META.setTableName(IceRecordConstants.CoreTableNames.SYS_DICTIONARY);
        SYS_DICTIONARY_META.addUniqueFieldsMeta(new FieldMeta[] {
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.SYS_ID,
                        IceRecordConstants.CoreFieldTypeNames.PRIMARY).build(),
                FieldMeta
                        .createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.COLLECTION,
                                IceRecordConstants.CoreFieldTypeNames.REFERENCE)
                        .whichIsMandatory().build(),
                FieldMeta
                        .createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.NAME,
                                IceRecordConstants.CoreFieldTypeNames.STRING)
                        .whichIsMandatory().build(),
                FieldMeta
                        .createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.LABEL,
                                IceRecordConstants.CoreFieldTypeNames.STRING)
                        .whichIsMandatory().build(),
                FieldMeta
                        .createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.TYPE,
                                IceRecordConstants.CoreFieldTypeNames.FIELD_TYPE_CHOICE)
                        .whichIsMandatory().build(),
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.MAX_LENGTH,
                        IceRecordConstants.CoreFieldTypeNames.POSITIVE_INTEGER).build(),
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.READONLY,
                        IceRecordConstants.CoreFieldTypeNames.BOOLEAN).build(),
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.MANDATORY,
                        IceRecordConstants.CoreFieldTypeNames.BOOLEAN).build(),
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.DISPLAY,
                        IceRecordConstants.CoreFieldTypeNames.BOOLEAN).build(),
                FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.UNIQUE,
                        IceRecordConstants.CoreFieldTypeNames.BOOLEAN).build(),
                FieldMeta.createWithFieldNameAndFieldType(
                        IceRecordConstants.FieldNames.DEFAULT_VALUE,
                        IceRecordConstants.CoreFieldTypeNames.STRING).build() });
    }

    private IConnectionManager connManager = IceRecordEnv.getExtComponent()
            .createConnectionManager();

    private DataSource dataSource = connManager.getDataSource();;

    private boolean hasError;

    private IceRecordException lastError;

    public boolean setupSysTablesIfNeeded() {
        boolean isAnyChangesDone = false;
        if (!connManager.isTableExists(IceRecordConstants.CoreTableNames.SYS_COLLECTION)) {
            isAnyChangesDone = true;
            createTable(SYS_COLLECTION_META);
        }
        // TODO
        return isAnyChangesDone;
    }

    public boolean hasError() {
        return hasError;
    }

    public IceRecordException getLastError() {
        try {
            return lastError;
        } finally {
            hasError = false;
            lastError = null;
        }
    }

    public TableMeta getTableMeta(String tableName) {
        checkNotNull(tableName);
        if (tableName.equals(IceRecordConstants.CoreTableNames.SYS_COLLECTION))
            return SYS_COLLECTION_META;
        if (tableName.equals(IceRecordConstants.CoreTableNames.SYS_DICTIONARY))
            return SYS_DICTIONARY_META;

        QueryRunner runner = new QueryRunner(dataSource);
        ResultSetHandler<List<TableFieldMetaCombinedBean>> h = new BeanListHandler<TableFieldMetaCombinedBean>(
                TableFieldMetaCombinedBean.class);
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ").append("c.").append(IceRecordConstants.FieldNames.NAME)
                    .append(" tableName, c.").append(IceRecordConstants.FieldNames.LABEL)
                    .append(" tableLabel, c.").append(IceRecordConstants.FieldNames.EXTENDS)
                    .append(" parentTableName, d.").append(IceRecordConstants.FieldNames.NAME)
                    .append(" fieldName, d.").append(IceRecordConstants.FieldNames.LABEL)
                    .append(" fieldLabel, d.").append(IceRecordConstants.FieldNames.TYPE)
                    .append(" fieldType, d.").append(IceRecordConstants.FieldNames.MAX_LENGTH)
                    .append(" maxLength, d.").append(IceRecordConstants.FieldNames.READONLY)
                    .append(" readOnly, d.").append(IceRecordConstants.FieldNames.MANDATORY)
                    .append(" mandatory, d.").append(IceRecordConstants.FieldNames.DISPLAY)
                    .append(" display, d.").append(IceRecordConstants.FieldNames.UNIQUE)
                    .append(" unique, d.").append(IceRecordConstants.FieldNames.DEFAULT_VALUE)
                    .append(" defaultValue FROM ")
                    .append(IceRecordConstants.CoreTableNames.SYS_COLLECTION).append(" c, ")
                    .append(IceRecordConstants.CoreTableNames.SYS_DICTIONARY).append(" d WHERE c.")
                    .append(IceRecordConstants.FieldNames.SYS_ID).append(" = d.")
                    .append(IceRecordConstants.FieldNames.COLLECTION).append(" AND c.name = ?");
            List<TableFieldMetaCombinedBean> res = runner.query(sql.toString(), h, tableName);
            if (res.isEmpty()) {
                throw new IllegalArgumentException("Invalid table");
            }
            TableMeta m = new TableMeta();
            boolean isFirst = true;
            for (TableFieldMetaCombinedBean b : res) {
                if (isFirst) {
                    m.parentTableName = b.parentTableName;
                    m.setTableName(b.tableName);
                    m.tableLabel = b.tableLabel;
                    isFirst = false;
                }
                FieldMeta.Builder fmb = FieldMeta
                        .createWithFieldNameAndFieldType(b.fieldName, b.fieldType)
                        .withFieldLabel(b.fieldLabel).withMaxLength(b.maxLength)
                        .withDefaultExpression(b.defaultValue);
                if (b.display)
                    fmb.whichIsDisplayField();
                if (b.mandatory)
                    fmb.whichIsMandatory();
                if (b.readOnly)
                    fmb.whichIsReadonly();
                if (b.unique)
                    fmb.whichIsUnique();
                m.addUniqueFieldMeta(fmb.build());
            }
        } catch (SQLException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return null;
    }

    public boolean createTable(TableMeta meta) {
        checkNotNull(meta);
        String sql = getCreateTableSql(meta);
        QueryRunner runner = new QueryRunner(this.dataSource);
        try {
            runner.update(sql);
            return true;
        } catch (SQLException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean createTables(List<TableMeta> metas) {
        checkNotNull(metas);
        StringBuffer bf = new StringBuffer();
        for (TableMeta m : metas) {
            String sql = getCreateTableSql(m);
            bf.append(sql);
        }

        QueryRunner runner = new QueryRunner(this.dataSource);
        try {
            runner.batch(bf.toString(), null);
            return true;
        } catch (SQLException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return false;
    }

    private String entityName(String e) {
        return "`" + e + "`";
    }

    private String getCreateTableSql(Table.TableMeta meta) {
        StringBuffer bf = new StringBuffer();
        bf.append("CREATE TABLE IF NOT EXISTS ").append(entityName(meta.getTableName()))
                .append(" (");
        FieldMeta pk = null;
        boolean isFirst = true;
        for (FieldMeta f : meta.getFields()) {
            FieldType ft = f.getFieldType();
            if (ft.isPrimary())
                pk = f;
            if (isFirst) {
                isFirst = false;
            } else {
                bf.append(",");
            }
            bf.append(entityName(f.getFieldName())).append(" ").append(getScalarSqlForDDL(f));
        }
        if (pk != null)
            bf.append(",PRIMARY KEY (").append(entityName(pk.getFieldName())).append(")");
        bf.append(");\n");
        return bf.toString();
    }

    private String getScalarSqlForDDL(FieldMeta f) {
        FieldType ft = f.getFieldType();
        StringBuffer b = new StringBuffer();
        if (FieldType.ScalarType.STRING.equals(ft.getScalarType())) {
            b.append("VARCHAR(").append(f.getMaxLength()).append(") ");
            if (ft.isPrimary())
                b.append(" ").append(" NOT NULL");
            return b.toString();
        }
        throw new IllegalArgumentException("Given field is has unsupported scalar type");
    }

    public int updateMultipleRecords(TableMeta meta, String whereClauseSql,
            Collection<Field<?>> updatedFields) {
        boolean isFirst = true;
        StringBuffer b = new StringBuffer();
        b.append("UPDATE ").append(entityName(meta.getTableName())).append(" SET ");
        for (Field<?> f : updatedFields) {
            if (isFirst)
                isFirst = false;
            else
                b.append(",");
            b.append(" ").append(entityName(f.getFieldName())).append(" = ")
                    .append(getScalarValueForAssigmentSql(f.getScalarValue()));
        }
        b.append(" WHERE ").append(whereClauseSql);
        QueryRunner runner = new QueryRunner(dataSource);
        try {
            return runner.update(b.toString());
        } catch (SQLException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return 0;
    }

    private String getScalarValueForAssigmentSql(Object o) {
        if (o == null)
            return "NULL";
        if (o instanceof Integer || o instanceof Float || o instanceof Double)
            return o.toString();
        else if (o instanceof String)
            return getSqlStringLiteral(o.toString(), null);
        else
            throw new IllegalArgumentException(
                    "Unsupported array constant. Expected one of Integer, Float, Double or String but got "
                            + o.getClass());
    }

    public IceRecord queryTable(WhereClause whereClause) {
        StringBuffer b = new StringBuffer();
        String queryTableName = whereClause.getFromInfo().getTableName();
        b.append("SELECT ").append(getSelectAttributesSql(whereClause)).append(" FROM ")
                .append(entityName(queryTableName)).append(" WHERE ");

        String whereClauseSql = createSqlFromWhereClause(whereClause);
        IceRecord record = new IceRecord(getTableMeta(whereClause.getFromInfo().getTableName()),
                whereClauseSql);
        if (whereClauseSql != null) {
            QueryRunner runner = new QueryRunner(dataSource);
            try {
                runner.query(b.append(whereClauseSql).toString(), record.getRSHandler());
            } catch (SQLException e) {
                hasError = true;
                lastError = IceRecordException.wrap(e);
                record.setLastError(lastError);
                e.printStackTrace();
            }
        }
        return record;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String createSqlFromWhereClause(WhereClause whereClause) {
        String queryTableName = whereClause.getFromInfo().getTableName();
        ParsedExpression exp;
        try {
            exp = new WhereClauseParser(whereClause.getClause()).parse();
            Map<String, Object> paramMap = whereClause.getParams();
            for (FieldExpression fe : exp.paramFields) {
                Object v = paramMap.get(fe.field.getQualifiedFieldName());
                if (v == null)
                    v = paramMap.get(fe.field.fieldName);
                if (v != null) {
                    String tableName = fe.field.tableName;
                    if (tableName != null) {
                        for (SubQuery s : exp.allSubQueries) {
                            if (tableName.equals(s.alias)) {
                                tableName = s.table;
                                break;
                            }
                        }
                    }
                    if (tableName == null)
                        tableName = queryTableName;
                    TableMeta tm = getTableMeta(tableName);
                    FieldMeta fm = tm.getFieldMeta(fe.field.fieldName);
                    Field f = fm.getFieldInstance();
                    try {
                        if (v instanceof List) {
                            List<Object> newV = new ArrayList<>();
                            for (Object v1 : ((List) v)) {
                                f.setValue(v1);
                                newV.add(f.getScalarValue());
                            }
                            v = newV;
                        } else {
                            f.setValue(v);
                            v = f.getScalarValue();
                        }
                        ((WhereClauseParser.ParamConst) fe.value).val = v;
                    } catch (Exception e) {
                        hasError = true;
                        lastError = IceRecordException.wrap(e);
                        e.printStackTrace();
                    }
                }
            }
            return createSqlFromCondition(exp.rootCondition);
        } catch (ParseException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return null;
    }

    private String createSqlFromCondition(WhereClauseParser.Condition cond) {
        StringBuffer b = new StringBuffer();
        if (cond.terms.size() == 1)
            b.append("(").append(createSqlFromQueryTerm(cond.terms.get(0))).append(")");
        for (int i = 0; i < cond.ops.size(); i++) {
            b.append("(").append(createSqlFromQueryTerm(cond.terms.get(i))).append(")");
            if (cond.ops.get(i).equals(WhereClauseParser.AndOrOp.AND))
                b.append(" AND ");
            else
                b.append(" OR ");
            b.append("(").append(createSqlFromQueryTerm(cond.terms.get(i + 1))).append(")");
        }
        return b.toString();
    }

    private String createSqlFromQueryTerm(WhereClauseParser.QueryTerm queryTerm) {
        if (queryTerm.conditionExp != null)
            return createSqlFromCondition(queryTerm.conditionExp);
        else {
            return createSqlFromFieldExp(queryTerm.fieldExp);
        }
    }

    private String createSqlFromFieldExp(WhereClauseParser.FieldExpression fieldExp) {
        StringBuffer b = new StringBuffer();
        if (fieldExp.lhsValue != null && fieldExp.value != null) {
            boolean isRhsArray = fieldExp.value instanceof WhereClauseParser.ArrayConst;
            LikeCharPos pos = getLikeCharPos(fieldExp.op);
            String op;
            if (fieldExp.op.equals(WhereClauseParser.BinaryOp.CONTAINS)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.ENDSWITH)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.LIKE)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.STARTSWITH))
                op = " LIKE ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.EQ)) {
                op = isRhsArray ? " IN " : " = ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.IN)) {
                op = isRhsArray ? " IN " : " = ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.NEQ)) {
                op = isRhsArray ? " NOT IN " : " <> ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.NOT_IN)) {
                op = isRhsArray ? " NOT IN " : " <> ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.GT))
                op = " > ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.GTE))
                op = " >= ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.LT))
                op = " < ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.LTE))
                op = " <= ";
            else
                throw new IllegalArgumentException("Unsupported Operator. Given: " + fieldExp.op);

            String v = createSqlFromValue(fieldExp.value, pos);
            if (v != null)
                return b.append(createSqlFromValue(fieldExp.lhsValue, null)).append(op).append(v)
                        .toString();
            else
                return "1=1";
        } else if (fieldExp.field != null && fieldExp.rhsField != null) {
            String op;
            if (fieldExp.op.equals(WhereClauseParser.BinaryOp.EQ)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.IN)) {
                op = " = ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.NEQ)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.NOT_IN)) {
                op = " <> ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.GT))
                op = " > ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.GTE))
                op = " >= ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.LT))
                op = " < ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.LTE))
                op = " <= ";
            else
                throw new IllegalArgumentException("Unsupported Operator. Given: " + fieldExp.op);

            return b.append(fieldPairToSql(fieldExp.field)).append(op)
                    .append(fieldPairToSql(fieldExp.rhsField)).toString();
        } else if (fieldExp.field != null && fieldExp.value != null) {
            boolean isRhsArray = fieldExp.value instanceof WhereClauseParser.ArrayConst;
            LikeCharPos pos = getLikeCharPos(fieldExp.op);
            String op;
            if (fieldExp.op.equals(WhereClauseParser.BinaryOp.CONTAINS)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.ENDSWITH)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.LIKE)
                    || fieldExp.op.equals(WhereClauseParser.BinaryOp.STARTSWITH))
                op = " LIKE ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.EQ)) {
                op = isRhsArray ? " IN " : " = ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.IN)) {
                op = isRhsArray ? " IN " : " = ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.NEQ)) {
                op = isRhsArray ? " NOT IN " : " <> ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.NOT_IN)) {
                op = isRhsArray ? " NOT IN " : " <> ";
            } else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.GT))
                op = " > ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.GTE))
                op = " >= ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.LT))
                op = " < ";
            else if (fieldExp.op.equals(WhereClauseParser.BinaryOp.LTE))
                op = " <= ";
            else
                throw new IllegalArgumentException("Unsupported Operator. Given: " + fieldExp.op);

            String v = createSqlFromValue(fieldExp.value, pos);
            if (v != null)
                return b.append(fieldPairToSql(fieldExp.field)).append(op).append(v).toString();
            else
                return "1=1";
        }
        throw new IllegalArgumentException("Unsupported FieldExpression. Given: " + fieldExp);
    }

    private String fieldPairToSql(WhereClauseParser.FieldPair fp) {
        String s = entityName(fp.fieldName);
        if (fp.tableName != null)
            return entityName(fp.tableName) + "." + s;
        else
            return s;
    }

    private LikeCharPos getLikeCharPos(WhereClauseParser.Op op) {
        if (op.equals(WhereClauseParser.BinaryOp.CONTAINS))
            return LikeCharPos.BOTH;
        else if (op.equals(WhereClauseParser.BinaryOp.ENDSWITH))
            return LikeCharPos.END;
        else if (op.equals(WhereClauseParser.BinaryOp.STARTSWITH))
            return LikeCharPos.START;
        return null;
    }

    private enum LikeCharPos {
        BOTH, END, START, NONE
    }

    private String createSqlFromValue(WhereClauseParser.Value value, LikeCharPos pos) {
        if (value instanceof WhereClauseParser.FloatConst
                || value instanceof WhereClauseParser.IntConst) {
            Object v = value.getValue();
            return v.toString();
        } else if (value instanceof WhereClauseParser.StringConst) {
            return getSqlStringLiteral(value.getValue().toString(), pos);
        } else if (value instanceof WhereClauseParser.ArrayConst) {
            List<Object> v = ((WhereClauseParser.ArrayConst) value).getValue();
            StringBuffer b = new StringBuffer("(");
            for (Object o : v) {
                b.append(getSqlForScalarsInWhereClause(o, null));
            }
            b.append(")");
            return b.toString();
        } else if (value instanceof WhereClauseParser.SubQuery) {
            WhereClauseParser.SubQuery v = (WhereClauseParser.SubQuery) value;
            StringBuffer b = new StringBuffer("( SELECT ");
            b.append(entityName(v.selectFieldName)).append(" FROM ").append(entityName(v.table));
            if (v.alias != null)
                b.append(v.alias);
            return b.append(" WHERE ").append(createSqlFromCondition(v.condition)).append(" )")
                    .toString();
        } else if (value instanceof WhereClauseParser.ParamConst) {
            Object v = ((WhereClauseParser.ParamConst) value).getValue();
            if (v == null) // Means user missed to provide value for this.
                           // Ignore.
                return null;
            return getSqlForScalarsInWhereClause(v, pos);
        }
        throw new IllegalArgumentException("Unsupported Value. Given: " + value);
    }

    private String getSqlForScalarsInWhereClause(Object o, LikeCharPos pos) {
        if (o instanceof Integer || o instanceof Float || o instanceof Double)
            return o.toString();
        else if (o instanceof String)
            return getSqlStringLiteral(o.toString(), pos);
        else if (o instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> l = (List<Object>) o;
            StringBuffer b = new StringBuffer("(");
            for (Object oi : l) {
                b.append(getSqlForScalarsInWhereClause(oi, null));
            }
            b.append(")");
            return b.toString();
        } else
            throw new IllegalArgumentException(
                    "Unsupported array constant. Expected one of Integer, Float, Double or String but got "
                            + o.getClass());
    }

    private String getSqlStringLiteral(String s, LikeCharPos pos) {
        if (pos != null) {
            if (LikeCharPos.BOTH.equals(pos))
                s = "%" + s + "%";
            else if (LikeCharPos.END.equals(pos))
                s = s + "%";
            else if (LikeCharPos.START.equals(pos))
                s = "%" + s;
        }
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }

    private String getSelectAttributesSql(WhereClause whereClause) {
        // TODO
        return "*";
    }

    static class TableFieldMetaCombinedBean {
        String tableLabel;

        String tableName;

        String parentTableName;

        String fieldName;

        String fieldLabel;

        String fieldType;

        int maxLength;

        boolean readOnly;

        boolean mandatory;

        boolean display;

        boolean unique;

        String defaultValue;
    }
}
