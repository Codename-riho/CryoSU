/// Bottom-bar pages, in pager order (matches the Kotlin pager).
#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum Page {
    Home = 0,
    SuperUser = 1,
    Module = 2,
    Settings = 3,
}

impl Page {
    pub const COUNT: usize = 4;

    /// Target page for the Home status tiles (ROOT -> SuperUser, MODULE -> Module).
    pub fn for_tile(tile: Tile) -> Page {
        match tile {
            Tile::Root => Page::SuperUser,
            Tile::Module => Page::Module,
        }
    }

    pub fn index(self) -> usize {
        self as usize
    }
}

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
pub enum Tile {
    Root,
    Module,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn tiles_navigate_to_correct_tab() {
        assert_eq!(Page::for_tile(Tile::Root), Page::SuperUser);
        assert_eq!(Page::for_tile(Tile::Module), Page::Module);
        assert_eq!(Page::Module.index(), 2);
        assert_eq!(Page::COUNT, 4);
    }
}
