const API_BASE_URL = '/api';
let authToken = localStorage.getItem('authToken');
let userRole = localStorage.getItem('userRole');

const app = {
    // --- Funções de Utilidade ---
    showMessage(elementId, message, type = 'error') {
        const element = document.getElementById(elementId);
        element.textContent = message;
        element.className = `message ${type}`;
        element.style.display = 'block';
        setTimeout(() => element.style.display = 'none', 5000);
    },

    // --- Funções de Autenticação ---
    updateUI() {
        const authSection = document.getElementById('auth-section');
        const appSection = document.getElementById('app-section');
        const userName = document.getElementById('user-name');
        const userRoleEl = document.getElementById('user-role');
        const visibilidadeSelect = document.getElementById('acesso-visibilidade');

        if (authToken) {
            authSection.style.display = 'none';
            appSection.style.display = 'block';
            userName.textContent = localStorage.getItem('userName');
            userRoleEl.textContent = userRole;

            // Desabilita a opção COMPARTILHADA para usuários normais
            const compartilhadaOption = visibilidadeSelect.querySelector('option[value="COMPARTILHADA"]');
            if (compartilhadaOption) {
                compartilhadaOption.disabled = userRole !== 'ROLE_ADMIN';
            }

            this.fetchAcessos();
            this.fetchNotificacoes();
        } else {
            authSection.style.display = 'block';
            appSection.style.display = 'none';
        }
    },

    async login() {
        const email = document.getElementById('email').value;
        const senha = document.getElementById('senha').value;

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, senha })
            });

            const data = await response.json();

            if (response.ok) {
                authToken = data.token;
                userRole = data.role;
                localStorage.setItem('authToken', authToken);
                localStorage.setItem('userName', data.nome);
                localStorage.setItem('userRole', userRole);
                this.showMessage('auth-message', 'Login bem-sucedido!', 'success');
                this.updateUI();
            } else {
                this.showMessage('auth-message', data.mensagem || 'Erro no login.');
            }
        } catch (error) {
            this.showMessage('auth-message', 'Erro de conexão com a API.');
            console.error('Erro de login:', error);
        }
    },

    logout() {
        authToken = null;
        userRole = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('userName');
        localStorage.removeItem('userRole');
        this.updateUI();
        document.getElementById('acessos-list').innerHTML = '';
        document.getElementById('notificacoes-list').innerHTML = '';
    },

    // --- Funções de Acessos (CRUD) ---
    async fetchAcessos() {
        if (!authToken) return;

        try {
            const response = await fetch(`${API_BASE_URL}/acessos`, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            const acessos = await response.json();
            const listElement = document.getElementById('acessos-list');
            listElement.innerHTML = '';

            if (response.ok) {
                acessos.forEach(acesso => {
                    const item = document.createElement('div');
                    item.className = 'acesso-item';
                    let status = '';
                    if (acesso.expirada) {
                        status = '<span style="color: red; font-weight: bold;">(EXPIRADA)</span>';
                    } else if (acesso.proximaExpiracao) {
                        status = '<span style="color: orange; font-weight: bold;">(PRÓXIMA DE EXPIRAR)</span>';
                    }

                    item.innerHTML = `
                        <strong>${acesso.titulo} ${status}</strong>
                        <p>URL: <a href="${acesso.url}" target="_blank">${acesso.url}</a></p>
                        <p>Login: ${acesso.login}</p>
                        <p>Visibilidade: ${acesso.tipoVisibilidade}</p>
                        <p>Proprietário: ${acesso.proprietarioNome}</p>
                        <p>Expira em: ${acesso.dataExpiracao || 'N/A'}</p>
                        <button class="btn" onclick="app.revelarSenha(${acesso.id})">Revelar Senha</button>
                        <button class="btn btn-danger" onclick="app.excluirAcesso(${acesso.id})">Excluir</button>
                        <span id="senha-${acesso.id}" style="margin-left: 10px; color: green;"></span>
                    `;
                    listElement.appendChild(item);
                });
            } else {
                listElement.innerHTML = `<p class="message error">${acessos.mensagem || 'Erro ao carregar acessos.'}</p>`;
            }
        } catch (error) {
            this.showMessage('acesso-message', 'Erro de conexão ao buscar acessos.');
            console.error('Erro ao buscar acessos:', error);
        }
    },

    async criarAcesso() {
        if (!authToken) return;

        const titulo = document.getElementById('acesso-titulo').value;
        const url = document.getElementById('acesso-url').value;
        const login = document.getElementById('acesso-login').value;
        const senha = document.getElementById('acesso-senha').value;
        const visibilidade = document.getElementById('acesso-visibilidade').value;
        const dataExpiracao = document.getElementById('acesso-expiracao').value;

        const novoAcesso = {
            titulo,
            url,
            login,
            senha,
            tipoVisibilidade: visibilidade,
            dataExpiracao: dataExpiracao || null
        };

        try {
            const response = await fetch(`${API_BASE_URL}/acessos`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(novoAcesso)
            });

            const data = await response.json();

            if (response.ok) {
                this.showMessage('acesso-message', 'Acesso criado com sucesso!', 'success');
                this.fetchAcessos();
            } else {
                this.showMessage('acesso-message', data.mensagem || 'Erro ao criar acesso.');
            }
        } catch (error) {
            this.showMessage('acesso-message', 'Erro de conexão ao criar acesso.');
            console.error('Erro ao criar acesso:', error);
        }
    },

    async excluirAcesso(id) {
        if (!authToken || !confirm('Tem certeza que deseja excluir este acesso?')) return;

        try {
            const response = await fetch(`${API_BASE_URL}/acessos/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            if (response.status === 204) {
                this.showMessage('acesso-message', 'Acesso excluído com sucesso!', 'success');
                this.fetchAcessos();
            } else {
                const data = await response.json();
                this.showMessage('acesso-message', data.mensagem || 'Erro ao excluir acesso.');
            }
        } catch (error) {
            this.showMessage('acesso-message', 'Erro de conexão ao excluir acesso.');
            console.error('Erro ao excluir acesso:', error);
        }
    },

    async revelarSenha(id) {
        if (!authToken) return;

        try {
            const response = await fetch(`${API_BASE_URL}/acessos/${id}/revelar`, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            const data = await response.json();
            const senhaElement = document.getElementById(`senha-${id}`);

            if (response.ok) {
                senhaElement.textContent = `Senha: ${data.senha}`;
                setTimeout(() => senhaElement.textContent = '', 10000); // Esconde após 10s
            } else {
                senhaElement.textContent = data.mensagem || 'Erro ao revelar senha.';
                senhaElement.style.color = 'red';
            }
        } catch (error) {
            console.error('Erro ao revelar senha:', error);
        }
    },

    // --- Funções de Notificação ---
    async fetchNotificacoes() {
        if (!authToken) return;

        try {
            const response = await fetch(`${API_BASE_URL}/notificacoes`, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            });

            const notificacoes = await response.json();
            const listElement = document.getElementById('notificacoes-list');
            listElement.innerHTML = '';

            if (response.ok) {
                notificacoes.forEach(notificacao => {
                    const item = document.createElement('li');
                    item.className = notificacao.tipo;
                    item.textContent = `[${notificacao.tipo}] ${notificacao.mensagem} (${new Date(notificacao.data).toLocaleTimeString()})`;
                    listElement.appendChild(item);
                });
            } else {
                listElement.innerHTML = `<li class="error">${notificacoes.mensagem || 'Erro ao carregar notificações.'}</li>`;
            }
        } catch (error) {
            console.error('Erro ao buscar notificações:', error);
        }
    }
};

// Inicializa a UI ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    app.updateUI();
});
