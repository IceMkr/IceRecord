import com.applegrew.icemkr.record.DaggerIceRecordExtensionComponent;
import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.IceRecordExtensionsModule;
import com.applegrew.icemkr.record.dialect.Schema;
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

        Schema.select(config.dbName).createIfNotExists().butIfFailedCall(e -> {
            System.err.println(e);
            return null;
        }).elseCall(() -> {
            if (!Table.setupSysTablesIfNeeded().hasError())
                Table.createIfNotExists("test")
                        .withFields(
                                FieldMeta
                                        .createWithFieldNameAndFieldType("xyz",
                                                IceRecordConstants.CoreFieldTypeNames.STRING)
                                        .build())
                        .commit();
        });

    }

}
