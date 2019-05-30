package ie.corballis.fixtures.io.write;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;

import java.io.IOException;

import static com.fasterxml.jackson.core.util.DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;

public class JsonFixturesPrettyPrinter extends DefaultPrettyPrinter {

    public JsonFixturesPrettyPrinter() {
    }

    public JsonFixturesPrettyPrinter(JsonFixturesPrettyPrinter prettyPrinter) {
        super(prettyPrinter);
        indentArraysWith(SYSTEM_LINEFEED_INSTANCE);
    }

    @Override
    public DefaultPrettyPrinter withSeparators(Separators separators) {
        super.withSeparators(separators);
        _objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
        return this;
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(_separators.getArrayValueSeparator());
        _arrayIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public DefaultPrettyPrinter createInstance() {
        return new JsonFixturesPrettyPrinter(this);
    }
}
