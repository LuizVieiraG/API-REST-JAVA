const state = {
  authors: [],
  books: []
};

const els = {
  authorForm: document.querySelector("#authorForm"),
  authorId: document.querySelector("#authorId"),
  authorName: document.querySelector("#authorName"),
  authorEmail: document.querySelector("#authorEmail"),
  authorsList: document.querySelector("#authorsList"),
  authorCount: document.querySelector("#authorCount"),
  clearAuthorButton: document.querySelector("#clearAuthorButton"),
  bookForm: document.querySelector("#bookForm"),
  bookId: document.querySelector("#bookId"),
  bookTitle: document.querySelector("#bookTitle"),
  bookIsbn: document.querySelector("#bookIsbn"),
  bookPrice: document.querySelector("#bookPrice"),
  bookAuthorIds: document.querySelector("#bookAuthorIds"),
  booksList: document.querySelector("#booksList"),
  bookCount: document.querySelector("#bookCount"),
  clearBookButton: document.querySelector("#clearBookButton"),
  refreshButton: document.querySelector("#refreshButton"),
  toast: document.querySelector("#toast")
};

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.mensagem || "Erro ao executar a operacao");
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

function showToast(message) {
  els.toast.textContent = message;
  els.toast.classList.add("show");
  window.setTimeout(() => els.toast.classList.remove("show"), 2600);
}

function authorNames(authors) {
  if (!authors || authors.length === 0) {
    return "Sem autores";
  }

  return authors.map((author) => author.nome).join(", ");
}

function renderAuthors() {
  els.authorCount.textContent = state.authors.length;

  if (state.authors.length === 0) {
    els.authorsList.innerHTML = "<p class=\"empty\">Nenhum autor cadastrado.</p>";
    return;
  }

  els.authorsList.innerHTML = state.authors.map((author) => `
    <article class="item">
      <div class="itemTitle">
        <strong>${author.nome}</strong>
        <span class="meta">ID ${author.id}</span>
      </div>
      <p class="meta">${author.email || "E-mail nao informado"}</p>
      <p class="meta">Livros: ${(author.livros || []).join(", ") || "nenhum"}</p>
      <div class="itemActions">
        <button type="button" class="secondary" data-edit-author="${author.id}">Editar</button>
        <button type="button" class="danger" data-delete-author="${author.id}">Excluir</button>
      </div>
    </article>
  `).join("");
}

function renderBooks() {
  els.bookCount.textContent = state.books.length;

  if (state.books.length === 0) {
    els.booksList.innerHTML = "<p class=\"empty\">Nenhum livro cadastrado.</p>";
    return;
  }

  els.booksList.innerHTML = state.books.map((book) => `
    <article class="item">
      <div class="itemTitle">
        <strong>${book.titulo}</strong>
        <span class="meta">ID ${book.id}</span>
      </div>
      <p class="meta">ISBN: ${book.isbn}</p>
      <p class="meta">Preco: R$ ${Number(book.preco).toFixed(2)}</p>
      <p class="meta">Autores: ${authorNames(book.autores)}</p>
      <div class="itemActions">
        <button type="button" class="secondary" data-edit-book="${book.id}">Editar</button>
        <button type="button" class="danger" data-delete-book="${book.id}">Excluir</button>
      </div>
    </article>
  `).join("");
}

async function loadData() {
  const [authors, books] = await Promise.all([
    api("/api/autores"),
    api("/api/livros")
  ]);

  state.authors = authors;
  state.books = books;
  renderAuthors();
  renderBooks();
}

function clearAuthorForm() {
  els.authorId.value = "";
  els.authorName.value = "";
  els.authorEmail.value = "";
  els.authorName.focus();
}

function clearBookForm() {
  els.bookId.value = "";
  els.bookTitle.value = "";
  els.bookIsbn.value = "";
  els.bookPrice.value = "";
  els.bookAuthorIds.value = "";
  els.bookTitle.focus();
}

function parseAuthorIds(value) {
  return value
    .split(",")
    .map((id) => Number(id.trim()))
    .filter((id) => Number.isInteger(id) && id > 0);
}

els.authorForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const id = els.authorId.value;
  const payload = {
    nome: els.authorName.value.trim(),
    email: els.authorEmail.value.trim()
  };

  try {
    await api(id ? `/api/autores/${id}` : "/api/autores", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(payload)
    });
    clearAuthorForm();
    await loadData();
    showToast(id ? "Autor atualizado." : "Autor cadastrado.");
  } catch (error) {
    showToast(error.message);
  }
});

els.bookForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const autorIds = parseAuthorIds(els.bookAuthorIds.value);
  if (autorIds.length === 0) {
    showToast("Informe pelo menos um ID de autor valido.");
    return;
  }

  const id = els.bookId.value;
  const payload = {
    titulo: els.bookTitle.value.trim(),
    isbn: els.bookIsbn.value.trim(),
    preco: Number(els.bookPrice.value),
    autorIds
  };

  try {
    await api(id ? `/api/livros/${id}` : "/api/livros", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(payload)
    });
    clearBookForm();
    await loadData();
    showToast(id ? "Livro atualizado." : "Livro cadastrado.");
  } catch (error) {
    showToast(error.message);
  }
});

els.authorsList.addEventListener("click", async (event) => {
  const editId = event.target.dataset.editAuthor;
  const deleteId = event.target.dataset.deleteAuthor;

  if (editId) {
    const author = state.authors.find((item) => String(item.id) === editId);
    if (!author) return;

    els.authorId.value = author.id;
    els.authorName.value = author.nome;
    els.authorEmail.value = author.email || "";
    els.authorName.focus();
  }

  if (deleteId && window.confirm("Excluir este autor?")) {
    try {
      await api(`/api/autores/${deleteId}`, { method: "DELETE" });
      await loadData();
      showToast("Autor excluido.");
    } catch (error) {
      showToast(error.message);
    }
  }
});

els.booksList.addEventListener("click", async (event) => {
  const editId = event.target.dataset.editBook;
  const deleteId = event.target.dataset.deleteBook;

  if (editId) {
    const book = state.books.find((item) => String(item.id) === editId);
    if (!book) return;

    els.bookId.value = book.id;
    els.bookTitle.value = book.titulo;
    els.bookIsbn.value = book.isbn;
    els.bookPrice.value = book.preco;
    els.bookAuthorIds.value = (book.autores || []).map((author) => author.id).join(", ");
    els.bookTitle.focus();
  }

  if (deleteId && window.confirm("Excluir este livro?")) {
    try {
      await api(`/api/livros/${deleteId}`, { method: "DELETE" });
      await loadData();
      showToast("Livro excluido.");
    } catch (error) {
      showToast(error.message);
    }
  }
});

els.clearAuthorButton.addEventListener("click", clearAuthorForm);
els.clearBookButton.addEventListener("click", clearBookForm);
els.refreshButton.addEventListener("click", async () => {
  await loadData();
  showToast("Dados atualizados.");
});

loadData().catch((error) => showToast(error.message));
