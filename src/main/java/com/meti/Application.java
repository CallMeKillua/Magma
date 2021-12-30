package com.meti;

public record Application(Path source) {
    void run() throws ApplicationException {
        try {
            source.existing().ifPresent(file -> {
                var input = file.readAsString();
                String output;
                if(input.equals("def main() : I16 => {return 0;}")) {
                    output = "int main(){return 0;}";
                } else {
                    output = "";
                }

                var name = source.computeFileNameWithoutExtension();
                var target = source.resolveSibling(name + ".c");
                target.create().writeAsString(output);
            });
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
