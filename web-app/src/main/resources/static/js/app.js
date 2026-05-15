'use strict';

const CSRF_TOKEN  = () => document.querySelector('meta[name="_csrf"]')?.content;
const CSRF_HEADER = () => document.querySelector('meta[name="_csrf_header"]')?.content;

function toast(msg, type = 'ok') {
    document.querySelectorAll('.toast').forEach(t => t.remove());
    const t = document.createElement('div');
    t.className = `toast toast--${type}`;
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => { t.style.opacity = '0'; setTimeout(() => t.remove(), 300); }, 4000);
}

async function api(url, method = 'POST', body = null) {
    const opts = { method, headers: { 'Content-Type':'application/json','X-Requested-With':'XMLHttpRequest' } };
    const h = CSRF_HEADER(), v = CSRF_TOKEN();
    if (h && v) opts.headers[h] = v;
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(url, opts);
    const json = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(json.error || `HTTP ${res.status}`);
    return json;
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.auto-hide').forEach(el => {
        setTimeout(() => { el.style.opacity='0'; setTimeout(() => el.remove(), 400); }, 5000);
    });
});

function openModal(id) {
    const m = document.getElementById(id);
    if (!m) return;
    m.classList.add('open');
    document.body.style.overflow = 'hidden';
    const f = m.querySelector('input:not([type=hidden]):not([type=checkbox]),textarea,select');
    if (f) setTimeout(() => f.focus(), 60);
}

function closeModal(id) {
    const m = document.getElementById(id);
    if (m) { m.classList.remove('open'); document.body.style.overflow = ''; }
}

document.addEventListener('keydown', e => {
    if (e.key === 'Escape')
        document.querySelectorAll('.modal.open').forEach(m => m.classList.remove('open'));
});

function collectTagIds(checksContainerId, hiddenInputId) {
    const container = document.getElementById(checksContainerId);
    const hidden    = document.getElementById(hiddenInputId);
    if (!container || !hidden) return;
    const ids = [...container.querySelectorAll('input[type=checkbox]:checked')]
        .map(cb => cb.value)
        .join(',');
    hidden.value = ids;
}

async function startTask(btn) {
    const id = btn.dataset.id;
    btn.disabled = true;
    try {
        await api(`/api/tasks/${id}/start`);
        const card = document.getElementById('task-' + id);
        if (card) {
            card.querySelector('.badge-s').className = 'badge-s badge-s--processing';
            card.querySelector('.badge-s').textContent = 'PROCESSING';
            btn.remove();
        }
        toast('Задача в работе');
    } catch (e) { toast(e.message, 'err'); btn.disabled = false; }
}

async function completeTask(btn) {
    const id = btn.dataset.id;
    btn.disabled = true;
    try {
        await api(`/api/tasks/${id}/complete`);
        const card = document.getElementById('task-' + id);
        if (card) {
            card.classList.add('card--done');
            card.querySelector('.badge-s').className = 'badge-s badge-s--completed';
            card.querySelector('.badge-s').textContent = 'COMPLETED';
            card.querySelectorAll('.btn--g[data-id]').forEach(b => b.remove());
        }
        toast('Задача выполнена! +XP ✓');
    } catch (e) { toast(e.message, 'err'); btn.disabled = false; }
}

function openEditModal(btn) {
    const { id, title, desc, status, priority, due, tagids } = btn.dataset;

    document.getElementById('editForm').action = `/tasks/${id}/update`;
    document.getElementById('eTitle').value    = title || '';
    document.getElementById('eDesc').value     = desc  || '';

    const ps = document.getElementById('ePriority');
    if (ps) [...ps.options].forEach(o => o.selected = o.value === priority);

    const ss = document.getElementById('eStatus');
    if (ss) [...ss.options].forEach(o => o.selected = o.value === status);

    const dueEl = document.getElementById('eDue');
    if (due && due !== 'null') dueEl.value = due.substring(0, 16);
    else dueEl.value = '';

    const currentIds = (tagids || '').split(',').filter(Boolean);
    const container  = document.getElementById('editTagChecks');
    if (container) {
        container.querySelectorAll('input[type=checkbox]').forEach(cb => {
            cb.checked = currentIds.includes(cb.value);
        });
    }

    openModal('mEdit');
}

let _reminderId = null;

function openReminderModal(btn) {
    _reminderId = btn.dataset.id;
    document.getElementById('rTitle').textContent = btn.dataset.title || '';
    document.getElementById('rDate').value = '';
    openModal('mReminder');
}

async function submitReminder() {
    const dateStr = document.getElementById('rDate').value;
    if (!dateStr) { toast('Выберите дату', 'err'); return; }
    try {
        await api('/api/reminders', 'POST', { taskId: _reminderId, reminderDate: dateStr });
        closeModal('mReminder');
        toast('Напоминание установлено ⏰');
    } catch (e) { toast(e.message, 'err'); }
}

function openTagEdit(btn) {
    const { id, name, desc, color } = btn.dataset;
    document.getElementById('tagEditForm').action = `/tags/${id}/update`;
    document.getElementById('teName').value  = name  || '';
    document.getElementById('teDesc').value  = desc  || '';
    document.getElementById('teColor').value = color || '#94a3b8';
    openModal('mTagEdit');
}

async function runAi() {
    const btn   = document.getElementById('aiBtn');
    const panel = document.getElementById('aiPanel');
    const body  = document.getElementById('aiBody');

    btn.disabled = true;
    btn.textContent = '⏳...';
    body.innerHTML  = '<p class="muted">Анализирую задачи…</p>';
    panel.style.display = 'block';

    try {
        const data = await api('/api/ai/prioritize');
        renderAi(data, body);
    } catch (e) {
        body.innerHTML = `<p style="color:#991b1b">Ошибка: ${e.message}</p>`;
        toast('AI недоступен', 'err');
    } finally {
        btn.disabled = false;
        btn.textContent = '✨ AI';
    }
}

function renderAi(data, container) {
    let html = '';
    if (data.generalAdvice) {
        html += `<div class="ai-advice">${data.generalAdvice}</div>`;
    }
    const recs = (data.recommendations || [])
        .slice()
        .sort((a, b) => a.recommendedOrder - b.recommendedOrder);

    if (recs.length === 0 && !data.generalAdvice) {
        html = '<p class="muted">Нет рекомендаций</p>';
    }

    recs.forEach(r => {
        html += `<div class="ai-rec">
            <div class="ai-num">${r.recommendedOrder}</div>
            <div>
              <span class="badge-p badge-p--${(r.suggestedPriority||'middle').toLowerCase()}">${r.suggestedPriority||''}</span>
              <span style="margin-left:8px;font-size:13px;color:#6c757d">${r.reason||''}</span>
            </div>
          </div>`;
    });
    container.innerHTML = html;
}


let _drawerOpen = false;

async function toggleNotifDrawer() {
    const drawer = document.getElementById('notifDrawer');
    if (!drawer) return;

    _drawerOpen = !_drawerOpen;
    drawer.style.display = _drawerOpen ? 'flex' : 'none';

    if (_drawerOpen) await loadNotifications();
}

async function loadNotifications() {
    const list = document.getElementById('notifList');
    if (!list) return;

    try {
        const data = await api('/api/notifications', 'GET');
        renderNotifications(Array.isArray(data) ? data : []);
    } catch (e) {
        list.innerHTML = '<p class="muted" style="padding:16px;font-size:13px">Ошибка загрузки</p>';
    }
}

function renderNotifications(items) {
    const list  = document.getElementById('notifList');
    const badge = document.getElementById('notifBadge');
    if (!list) return;

    if (items.length === 0) {
        list.innerHTML = '<p class="muted" style="padding:16px;font-size:13px">Нет уведомлений</p>';
        if (badge) badge.style.display = 'none';
        return;
    }

    if (badge) {
        badge.style.display = 'flex';
        badge.textContent = items.length > 99 ? '99+' : String(items.length);
    }

    list.innerHTML = items.map(n => `
        <div class="notif-item" id="notif-${n.id || ''}">
            <span class="notif-item__type">${n.type || ''}</span>
            <span class="notif-item__title">${n.title || ''}</span>
            <span class="notif-item__body">${n.body || ''}</span>
            ${n.id ? `<button class="notif-item__read" onclick="markRead('${n.id}')">Прочитано</button>` : ''}
        </div>
    `).join('');
}

async function markRead(notifId) {
    try {
        // POST /api/notifications/{id}/read
        await api(`/api/notifications/${notifId}/read`, 'POST');

        const el = document.getElementById('notif-' + notifId);
        if (el) {
            el.style.opacity = '0';
            el.style.transition = 'opacity .3s';
            setTimeout(() => el.remove(), 300);
        }

        const badge = document.getElementById('notifBadge');
        if (badge) {
            const count = parseInt(badge.textContent) - 1;
            if (count <= 0) badge.style.display = 'none';
            else badge.textContent = String(count);
        }
    } catch (e) {
        toast('Не удалось отметить как прочитанное', 'err');
    }
}

document.addEventListener('keydown', e => {
    if (e.key === 'Escape' && _drawerOpen) {
        _drawerOpen = false;
        const drawer = document.getElementById('notifDrawer');
        if (drawer) drawer.style.display = 'none';
    }
});

document.addEventListener('DOMContentLoaded', async () => {
    const bell = document.querySelector('.notif-bell');
    if (!bell) return;

    try {
        const data  = await api('/api/notifications', 'GET');
        const items = Array.isArray(data) ? data : [];
        const badge = document.getElementById('notifBadge');
        if (badge && items.length > 0) {
            badge.style.display = 'flex';
            badge.textContent = items.length > 99 ? '99+' : String(items.length);
        }
    } catch (_) {

    }
});