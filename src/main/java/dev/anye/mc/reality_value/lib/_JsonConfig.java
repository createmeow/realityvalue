package dev.anye.mc.reality_value.lib;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;

public class _JsonConfig<T> extends _JsonSupport {
    private final boolean checkData;
    protected final String filePath;
    protected final String defaultData;
    protected final Type type;
    protected T datas;

    public _JsonConfig(String filePath, String defaultData, TypeToken<T> typeToken) {
        this(filePath, defaultData, typeToken, true);
    }

    public _JsonConfig(String filePath, String defaultData, TypeToken<T> typeToken, boolean checkData) {
        this.filePath = filePath;
        this.defaultData = defaultData;
        this.type = typeToken.getType();
        this.checkData = checkData;
        init();
    }

    public void init() {
        File file = new File(filePath);
        if (!file.exists()) {
            reset();
        } else {
            if (checkData)
                CheckData(defaultData, filePath);
        }
        load();
    }

    public void reset() {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(defaultData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() {
        datas = null;
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            datas = gson.fromJson(reader, this.type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(datas, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        init();
    }

    public T getDatas() {
        return datas;
    }
}