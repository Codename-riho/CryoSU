#ifndef __KSU_H_KLOG
#define __KSU_H_KLOG
#include <linux/version.h>

#if LINUX_VERSION_CODE < KERNEL_VERSION(3, 10, 0)
// for 3.10-
// https://github.com/torvalds/linux/commit/154c2670087bd7f54688274aca627433e4a7c181
// https://github.com/torvalds/linux/commit/1b2c289b4f9018f4bd54d22ab54aaeda9181cd2a
// The linux kernel below 3.10 may not include them, manually backport the 2 commit for them
#include <stdarg.h>
#include <linux/linkage.h>
#endif

#include <linux/printk.h>

#ifdef pr_fmt
#undef pr_fmt
#define pr_fmt(fmt) "KernelSU: " fmt
#endif

/*
 * Log yang membocorkan identitas manager / UID / hash sertifikat / PID.
 * Default: DIHILANGKAN (hardening, mengurangi data yang terbaca lewat dmesg).
 * Aktifkan lagi untuk debugging dengan CONFIG_KSU_VERBOSE_LOGS=y atau
 * CONFIG_KSU_DEBUG=y. Argumen tetap diperiksa kompiler (if (0)).
 */
#if defined(CONFIG_KSU_VERBOSE_LOGS) || defined(CONFIG_KSU_DEBUG)
#define pr_info_sens(fmt, ...) pr_info(fmt, ##__VA_ARGS__)
#else
#define pr_info_sens(fmt, ...)                                                                                         \
    do {                                                                                                               \
        if (0)                                                                                                         \
            pr_info(fmt, ##__VA_ARGS__);                                                                               \
    } while (0)
#endif

#endif
