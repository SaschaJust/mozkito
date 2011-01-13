package de.unisaarland.cs.st.reposuite.ppa.model;

import de.unisaarland.cs.st.reposuite.utils.specification.NonNegative;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;

public class JavaMethodDefinition extends JavaElementDefinition {

    private List<String> signature = new ArrayList(0);

    public JavaMethodDefinition(@NotNull final String fullQualifiedName, @NotNull final List<String> signature,
            @NotNull final String file, @NotNull final DateTime timestamp, @NotNull final JavaClassDefinition parent,
            @NonNegative final int startLine, @NonNegative final int endLine) {

        super(fullQualifiedName, file, timestamp, startLine, endLine, parent);

        this.signature = new ArrayList<String>(signature);
    }

    public List<String> getSignature() {
        return signature;
    }

    @Override
    public JavaClassDefinition getParent() {
        return (JavaClassDefinition) super.getParent();
    }
}
