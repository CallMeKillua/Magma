package com.meti;

import java.util.List;

public class StringScript implements Script {
    private final List<String> packageList;
    private final String extension;
    private final String content;

    public StringScript(List<String> packageList, String content) {
        this(packageList, "", content);
    }

    public StringScript(List<String> packageList, String extension, String content) {
        this.packageList = packageList;
        this.extension = extension;
        this.content = content;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public String stringifyPackage() {
        return String.join("_", packageList);
    }

    @Override
    public String asString() {
        return packageList.toString();
    }

    @Override
    public boolean hasExtensionOf(String extension) {
        return this.extension.equals(extension);
    }

    @Override
    public String read() {
        return content;
    }

    @Override
    public void write(Output output) {
    }
}
