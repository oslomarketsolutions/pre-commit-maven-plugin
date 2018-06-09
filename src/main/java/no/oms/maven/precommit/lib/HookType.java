package no.oms.maven.precommit.lib;

public enum HookType {
    preCommit, prePush, commitMsg;

    public String getValue() {
        if (this == preCommit) return "pre-commit";
        if (this == prePush) return "pre-push";
        if (this == commitMsg) return "commit-msg";
        throw new Error("The HookType " + this + " is not supported");
    }
}
