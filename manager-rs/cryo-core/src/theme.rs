/// Pale-blue Cryo seed color (ARGB), same as `CRYO_SEED_COLOR` in Kotlin.
pub const CRYO_SEED_COLOR: u32 = 0xFF5B9BD5;
/// Dynamic (Monet) color is off by default.
pub const DYNAMIC_COLOR_DEFAULT: bool = false;

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn defaults() {
        let (seed, dynamic) = (CRYO_SEED_COLOR, DYNAMIC_COLOR_DEFAULT);
        assert_eq!(seed, 0xFF5B9BD5);
        assert!(!dynamic);
    }
}
