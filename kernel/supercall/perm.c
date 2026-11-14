#include <linux/types.h>

#include "supercall/internal.h"
#include "manager/manager_identity.h"
#include "policy/allowlist.h"
#include "selinux/selinux.h"

#include "compat/kernel_compat.h"

bool only_manager(void)
{
    return is_manager();
}

bool only_root(void)
{
    return ksu_get_uid_t(current_uid()) == 0;
}

bool manager_or_root(void)
{
    return ksu_get_uid_t(current_uid()) == 0 || is_manager();
}

bool always_allow(void)
{
    return true;
}

bool allowed_for_su(void)
{
    bool is_allowed;

    // Layered check: even if the uid somehow matches an allowed uid or the
    // manager appid, a caller actually running in the isolated_app sandbox
    // is never eligible for su. This is independent of the uid-range check
    // already applied where root is granted (hook/setuid_hook.c).
    if (is_current_isolated_app_domain()) {
        return false;
    }

    is_allowed = is_manager() || ksu_is_allow_uid_for_current(ksu_get_uid_t(current_uid()));

    return is_allowed;
}
