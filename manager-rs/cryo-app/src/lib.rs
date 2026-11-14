//! Android UI shell for the Rust CryoSU manager (egui via eframe).
//! First slice: 4-tab navigation, theme defaults and the SuSFS chip row,
//! all driven by `cryo-core`. NOT yet compiled in this repo's CI history;
//! see MANAGER_RS.md.

use cryo_core::{nav::Page, susfs, theme};
use egui::{Color32, RichText};

struct CryoApp {
    page: Page,
    susfs_raw: String,
}

impl Default for CryoApp {
    fn default() -> Self {
        Self {
            page: Page::Home,
            susfs_raw: "CONFIG_KSU_SUSFS_SUS_PATH CONFIG_KSU_SUSFS_SUS_MOUNT CONFIG_KSU_SUSFS_SUS_KSTAT"
                .to_owned(),
        }
    }
}

fn seed() -> Color32 {
    let c = theme::CRYO_SEED_COLOR;
    Color32::from_rgb((c >> 16) as u8, (c >> 8) as u8, c as u8)
}

impl eframe::App for CryoApp {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        egui::TopBottomPanel::bottom("nav").show(ctx, |ui| {
            ui.horizontal(|ui| {
                for (p, label) in [
                    (Page::Home, "Home"),
                    (Page::SuperUser, "Superuser"),
                    (Page::Module, "Module"),
                    (Page::Settings, "Settings"),
                ] {
                    ui.selectable_value(&mut self.page, p, label);
                }
            });
        });
        egui::CentralPanel::default().show(ctx, |ui| match self.page {
            Page::Home => {
                ui.heading(RichText::new("CryoSU").color(seed()));
                ui.horizontal_wrapped(|ui| {
                    for f in susfs::simplify_features(&self.susfs_raw) {
                        ui.label(RichText::new(f).background_color(seed().gamma_multiply(0.25)));
                    }
                });
            }
            Page::SuperUser => {
                ui.heading("Superuser");
            }
            Page::Module => {
                ui.heading("Module");
            }
            Page::Settings => {
                ui.heading("Settings");
            }
        });
    }
}

#[cfg(target_os = "android")]
#[no_mangle]
fn android_main(app: android_activity::AndroidApp) {
    android_logger::init_once(android_logger::Config::default().with_max_level(log::LevelFilter::Info));
    let options = eframe::NativeOptions {
        android_app: Some(app),
        ..Default::default()
    };
    let _ = eframe::run_native("CryoSU", options, Box::new(|_| Ok(Box::<CryoApp>::default())));
}
