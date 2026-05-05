(function () {
    'use strict';

    function getCsrf() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        return tokenMeta && headerMeta
            ? { token: tokenMeta.content, header: headerMeta.content }
            : null;
    }

    function ensureToastContainer() {
        let c = document.querySelector('.toast-stack');
        if (!c) {
            c = document.createElement('div');
            c.className = 'toast-stack';
            document.body.appendChild(c);
        }
        return c;
    }

    function showToast(message, variant) {
        const container = ensureToastContainer();
        const el = document.createElement('div');
        el.className = `toast align-items-center text-bg-${variant || 'success'} border-0 show`;
        el.role = 'alert';
        el.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>`;
        container.appendChild(el);
        setTimeout(() => el.remove(), 4000);
    }

    window.brToast = showToast;

    async function checkAvailability(equipmentId, start, end) {
        const url = `/api/availability?equipmentId=${equipmentId}&start=${start}&end=${end}`;
        const resp = await fetch(url);
        if (!resp.ok) throw new Error('Помилка перевірки доступності');
        return resp.json();
    }

    document.querySelectorAll('[data-availability-check]').forEach(form => {
        const status = form.querySelector('[data-availability-status]');
        const equipmentId = form.dataset.availabilityCheck;
        const update = async () => {
            const start = form.querySelector('input[name=startDate]')?.value;
            const end = form.querySelector('input[name=endDate]')?.value;
            if (!start || !end || !status) return;
            try {
                const data = await checkAvailability(equipmentId, start, end);
                if (data.available > 0) {
                    status.className = 'text-success small';
                    status.textContent = `Доступно: ${data.available} од.`;
                } else {
                    status.className = 'text-danger small';
                    status.textContent = 'На обрані дати немає вільних одиниць';
                }
            } catch (e) {
                status.className = 'text-warning small';
                status.textContent = 'Не вдалося перевірити';
            }
        };
        form.querySelectorAll('input[type=date]').forEach(i => i.addEventListener('change', update));
        update();
    });

    document.querySelectorAll('[data-add-to-cart]').forEach(form => {
        form.addEventListener('submit', async (ev) => {
            ev.preventDefault();
            const csrf = getCsrf();
            const headers = { 'Content-Type': 'application/json' };
            if (csrf) headers[csrf.header] = csrf.token;
            const body = {
                equipmentId: form.querySelector('input[name=equipmentId]').value,
                quantity: parseInt(form.querySelector('input[name=quantity]').value || '1', 10),
                startDate: form.querySelector('input[name=startDate]').value,
                endDate: form.querySelector('input[name=endDate]').value
            };
            try {
                const resp = await fetch('/api/cart/items', { method: 'POST', headers, body: JSON.stringify(body) });
                const data = await resp.json();
                if (resp.ok) {
                    showToast('Додано в кошик', 'success');
                    document.querySelectorAll('[data-cart-count]').forEach(b => {
                        b.textContent = data.cartCount;
                        b.classList.remove('d-none');
                    });
                } else {
                    showToast(data.message || 'Помилка', 'danger');
                }
            } catch (e) {
                showToast('Не вдалося додати в кошик', 'danger');
            }
        });
    });
})();
