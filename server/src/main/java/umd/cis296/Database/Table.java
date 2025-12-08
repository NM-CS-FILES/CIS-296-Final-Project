package umd.cis296.Database;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import umd.cis296.objects.Idable;

public class Table<T extends Idable> {

    //
    //

    @FunctionalInterface
    private interface FieldSetter {
        void set(Object value, Field field, ResultSet result) throws Exception;
    }

    private static final Map<Class<?>, FieldSetter> FIELD_SETTERS = new HashMap<>();

    static {
        FIELD_SETTERS.put(int.class,     (value, field, result) -> field.setInt(value, result.getInt(field.getName())));
        FIELD_SETTERS.put(byte.class,    (value, field, result) -> field.setByte(value, result.getByte(field.getName())));
        FIELD_SETTERS.put(short.class,   (value, field, result) -> field.setShort(value, result.getShort(field.getName())));
        FIELD_SETTERS.put(long.class,    (value, field, result) -> field.setLong(value, result.getLong(field.getName())));
        FIELD_SETTERS.put(float.class,   (value, field, result) -> field.setFloat(value, result.getFloat(field.getName())));
        FIELD_SETTERS.put(double.class,  (value, field, result) -> field.setDouble(value, result.getDouble(field.getName())));
        FIELD_SETTERS.put(boolean.class, (value, field, result) -> field.setBoolean(value, result.getBoolean(field.getName())));
        FIELD_SETTERS.put(String.class,  (value, field, result) -> field.set(value, result.getString(field.getName())));
        FIELD_SETTERS.put(char.class,    (value, field, result) -> {
            String str = result.getString(field.getName());
            if (str != null && !str.isEmpty()) {
                field.setChar(value, str.charAt(0));
            }
        });
    }

    //
    //

    @FunctionalInterface
    private interface FieldGetter {
        String get(Object value, Field field) throws Exception;
    }

    private static final Map<Class<?>, FieldGetter> FIELD_GETTERS = new HashMap<>();

    static {
        FIELD_GETTERS.put(int.class,     (value, field) -> field.get(value).toString());
        FIELD_GETTERS.put(byte.class,    (value, field) -> field.get(value).toString());
        FIELD_GETTERS.put(short.class,   (value, field) -> field.get(value).toString());
        FIELD_GETTERS.put(long.class,    (value, field) -> field.get(value).toString());
        FIELD_GETTERS.put(float.class,   (value, field) -> field.get(value).toString());
        FIELD_GETTERS.put(double.class,  (value, field) -> field.get(value).toString());
        FIELD_GETTERS.put(boolean.class, (value, field) -> field.get(value).toString().toUpperCase());
        FIELD_GETTERS.put(String.class,  (value, field) -> "'" + field.get(value).toString().replace("'", "''") + "'");
        FIELD_GETTERS.put(char.class,    (value, field) -> "'" + field.get(value).toString() + "'");
    }

    //
    //

    @FunctionalInterface
    private interface FieldSqliteTypeGetter {
        String get(Field field) throws Exception;
    }

    private static final Map<Class<?>, FieldSqliteTypeGetter> FIELD_SQL_TYPE_GETTERS = new HashMap<>();

    static {
        FIELD_SQL_TYPE_GETTERS.put(int.class,     (field) -> "INTEGER");
        FIELD_SQL_TYPE_GETTERS.put(byte.class,    (field) -> "INTEGER");
        FIELD_SQL_TYPE_GETTERS.put(short.class,   (field) -> "INTEGER");
        FIELD_SQL_TYPE_GETTERS.put(long.class,    (field) -> "INTEGER");
        FIELD_SQL_TYPE_GETTERS.put(float.class,   (field) -> "REAL");
        FIELD_SQL_TYPE_GETTERS.put(double.class,  (field) -> "REAL");
        FIELD_SQL_TYPE_GETTERS.put(boolean.class, (field) -> "INTEGER");
        FIELD_SQL_TYPE_GETTERS.put(String.class,  (field) -> "TEXT");
        FIELD_SQL_TYPE_GETTERS.put(char.class,    (field) -> "TEXT");
    }

    //
    //

    private String name;
    private Class<T> clazz;

    //
    //

    private T constructObject(ResultSet result) throws Exception {
        T value = this.clazz.getDeclaredConstructor().newInstance();
        Field[] fields = this.clazz.getFields();

        for (Field field : fields) {
            boolean accessibility = field.canAccess(value);
            field.setAccessible(true);
            FIELD_SETTERS.get(field.getType()).set(value, field, result);
            field.setAccessible(accessibility);
        }

        return value;
    }

    private List<T> constructObjects(ResultSet result) throws Exception {
        List<T> resultsObjects = new ArrayList<>();

        while (result.next()) {
            try {
                resultsObjects.add(constructObject(result));
            } catch (Exception ex) {
                Logger.warn(ex);
            }
        }

        return resultsObjects;
    }

    //
    //

    
    private String getExistsSql() {
        return "SELECT name FROM sqlite_master WHERE type='table' AND name='" + this.getName() + "'";
    }

    private String getCreationFieldSql(Field field) throws Exception {
        return field.getName() + " " + FIELD_SQL_TYPE_GETTERS.get(field.getType()).get(field);
    }

    private String getCreationSql() throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + this.name + " (");
        sqlBuilder.append("id INTEGER PRIMARY KEY");

        for (Field field : this.clazz.getDeclaredFields()) {
            sqlBuilder.append(", ");
            sqlBuilder.append(getCreationFieldSql(field));
        }

        sqlBuilder.append(");");

        return sqlBuilder.toString();
    }

    private String getInsertionSql(T value) throws Exception {
        StringBuilder sqlBuilderInsert = new StringBuilder();
        StringBuilder sqlBuilderValues = new StringBuilder();

        sqlBuilderInsert.append("INSERT INTO " + this.name + " (");
        sqlBuilderValues.append("VALUES (");

        sqlBuilderInsert.append("id");
        sqlBuilderValues.append(value.getId());

        for (Field field : this.clazz.getDeclaredFields()) {
            sqlBuilderInsert.append(", ");
            sqlBuilderInsert.append(field.getName());

            boolean accessibility = field.canAccess(value);
            field.setAccessible(true);
            sqlBuilderValues.append(", ");
            sqlBuilderValues.append(FIELD_GETTERS.get(field.getType()).get(value, field));
            field.setAccessible(accessibility);
        }

        sqlBuilderInsert.append(") ");
        sqlBuilderValues.append(");");

        return sqlBuilderInsert.toString() + sqlBuilderValues.toString();
    }

    private String getSelectAllSql() {
        return "SELECT * FROM " + this.name + ";";
    }

    private String getSelectPageSql(int pageIndex, int pageSize) {
        return "SELECT * FROM " + this.name + " ORDER BY id LIMIT " + pageSize + " OFFSET " + (pageIndex * pageSize) + ";";
    }

    private String getSelectByIdSql(int id) {
        return "SELECT * FROM " + this.name + " WHERE id = " + id + ";";
    }

    private String getUpdateSql(T value) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("UPDATE " + this.name + " SET ");

        Field[] fields = this.clazz.getDeclaredFields();

        for (int i = 0; i != fields.length; i++) {
            if (i != 0) {
                sqlBuilder.append(", ");
            }

            boolean accessibility = fields[i].canAccess(value);
            fields[i].setAccessible(true);
            sqlBuilder.append(fields[i].getName() + " = " + FIELD_GETTERS.get(fields[i].getType()).get(value, fields[i]));
            fields[i].setAccessible(accessibility);
        }

        sqlBuilder.append(" WHERE id = " + value.getId() + ";");

        return sqlBuilder.toString();
    }

    private String getDeleteSql(int id) {
        return "DELETE FROM " + this.name + " WHERE id = " + id + ";";
    }

    //
    //

    public Table(String name, Class<T> clazz) {
        this.clazz = clazz;
        this.name = name;
    }

    public Table(Class<T> clazz) {
        this(clazz.getSimpleName(), clazz);
    }

    //
    //

    public String getName() {
        return this.name;
    }

    //
    //

    public void create() throws Exception {
        if (this.exists()) {
            return;
        }

        Database.instance().getConnection().createStatement().execute(getCreationSql());
    }

    public boolean exists() throws Exception {
        try (Statement statement = Database.instance().getConnection().createStatement()) {
            return statement.executeQuery(getExistsSql()).next();
        }
    }

    //
    //

    public void insert(T value) throws Exception {
        this.create();

        try (Statement statement = Database.instance().getConnection().createStatement()) {
            statement.execute(getInsertionSql(value));
        }
    }

    public List<T> getAll() throws Exception {
        this.create();

        List<T> values = new ArrayList<>();

        try (Statement statement = Database.instance().getConnection().createStatement()) {
            ResultSet results = statement.executeQuery(getSelectAllSql());
            values = constructObjects(results);
        }

        return values;
    }

    public List<T> getPage(int pageIndex, int pageSize) throws Exception {
        this.create();

        List<T> values = new ArrayList<>();

        try (Statement statement = Database.instance().getConnection().createStatement()) {
            ResultSet results = statement.executeQuery(getSelectPageSql(pageIndex, pageSize));
            values = constructObjects(results);
        }

        return values;
    }

    public T get(int id) throws Exception {
        this.create();

        try (Statement statement = Database.instance().getConnection().createStatement()) {
            ResultSet results = statement.executeQuery(getSelectByIdSql(id));

            if (!results.next()) {
                throw new Exception();
            }

            return constructObject(results);
        }
    }

    public void update(T value) throws Exception {
        this.create();

        try (Statement statement = Database.instance().getConnection().createStatement()) {
            statement.execute(getUpdateSql(value));
        }
    }

    public void delete(int id) throws Exception {
        this.create();

        try (Statement statement = Database.instance().getConnection().createStatement()) {
            statement.execute(getDeleteSql(id));
        }
    }

    public void delete(T value) throws Exception {
        this.delete(value.getId());
    }
}
