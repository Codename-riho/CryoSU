/// Port of Kotlin `simplifySuSFSFeatures`: turns a raw feature string such as
/// `CONFIG_KSU_SUSFS_SUS_PATH, CONFIG_KSU_SUSFS_SUS_MOUNT` into short,
/// de-duplicated, lower-case names (`sus_path`, `sus_mount`).
pub fn simplify_features(raw: &str) -> Vec<String> {
    let mut out: Vec<String> = Vec::new();
    for part in raw.split(|c: char| c.is_whitespace() || c == ',') {
        let part = part.trim();
        let part = part.strip_prefix("CONFIG_KSU_SUSFS_").unwrap_or(part);
        let part = part.strip_prefix("CONFIG_").unwrap_or(part);
        let name = part.to_lowercase();
        if !name.is_empty() && !out.contains(&name) {
            out.push(name);
        }
    }
    out
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn strips_prefixes_and_lowercases() {
        assert_eq!(
            simplify_features("CONFIG_KSU_SUSFS_SUS_PATH, CONFIG_KSU_SUSFS_SUS_MOUNT"),
            ["sus_path", "sus_mount"]
        );
    }

    #[test]
    fn dedups_and_ignores_blanks() {
        assert_eq!(
            simplify_features("  sus_kstat\n\nSUS_KSTAT ,, CONFIG_FOO "),
            ["sus_kstat", "foo"]
        );
    }

    #[test]
    fn empty_input() {
        assert!(simplify_features("").is_empty());
    }
}
