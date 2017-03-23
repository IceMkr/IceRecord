import com.applegrew.icemkr.record.DaggerIceRecordExtensionComponent;
import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.IceRecordExtensionsModule;
import com.applegrew.icemkr.record.dialect.IceRecord;
import com.applegrew.icemkr.record.dialect.IceRecordInsert;
import com.applegrew.icemkr.record.dialect.Insert;
import com.applegrew.icemkr.record.dialect.Schema;
import com.applegrew.icemkr.record.dialect.Select;
import com.applegrew.icemkr.record.dialect.Table;
import com.applegrew.icemkr.record.extension.impl.MySqlConnectionManager;
import com.applegrew.icemkr.record.field.FieldMeta;

public class TestMain {

    public static void main(String[] args) {
        MySqlConnectionManager.DBConfig config = new MySqlConnectionManager.DBConfig();
        config.dbName = "IceMkr";
        config.host = "localhost";
        config.password = "";
        config.port = 3306;
        config.userName = "root";
        IceRecordEnv.setExtComponent(DaggerIceRecordExtensionComponent.builder()
                .iceRecordExtensionsModule(new IceRecordExtensionsModule(config)).build());

        Schema.select(config.dbName).deleteIfExists().createIfNotExists().butIfFailedCall(e -> {
            System.err.println(e);
            return null;
        }).elseCall(() -> {
            if (!Table.setupSysTablesIfNeeded().hasError()) {
                Table.createIfNotExists("test")
                        .withFields(
                                FieldMeta
                                        .createWithFieldNameAndFieldType("xyz",
                                                IceRecordConstants.CoreFieldTypeNames.STRING)
                                        .build(),
                                FieldMeta
                                        .createWithFieldNameAndFieldType("boolf",
                                                IceRecordConstants.CoreFieldTypeNames.BOOLEAN)
                                        .build())
                        .commit();

                IceRecordInsert r = Insert.into("test");
                if (!r.isValid())
                    System.err.println("Table not valid!");
                r.setValue("xyz", "Something1");
                System.out.println(r.insert());

                r = Insert.into("test");
                if (!r.isValid())
                    System.err.println("Table not valid!");
                r.setValue("xyz", "Something2");
                r.setValue("boolf", true);
                System.out.println(r.insert());

                if (r.hasError())
                    System.err.println(r.getLastError());

                IceRecord r1 = Select.from("test").queryAll();
                if (!r1.isValid())
                    System.err.println("Problem with query!");
                while (r1.next())
                    System.out.println(r.getStringValue("xyz"));

                r1 = Select.from("test").where("boolf = ?").addParamValue("boolf", true).query();
                if (!r1.isValid())
                    System.err.println("Problem with query!");
                while (r1.next())
                    System.out.println(r.getStringValue("xyz"));

            }
        });

    }

}
