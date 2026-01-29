@param java.util.List<ru.mentee.power.crm.domain.Lead> leads
@param String search
@param String status

@template.layout.main(content = @`
<div class="bg-white rounded-lg shadow-md p-6">

    <div class="mb-4 flex gap-2">
        <a href="/leads"
           class="px-4 py-2 rounded ${status == null ? "bg-blue-500 text-white" : "bg-gray-200"}">
            Все
        </a>

        <a href="/leads?status=NEW"
           class="px-4 py-2 rounded ${"NEW".equals(status) ? "bg-blue-500 text-white" : "bg-gray-200"}">
            NEW
        </a>

        <a href="/leads?status=QUALIFIED"
           class="px-4 py-2 rounded ${"QUALIFIED".equals(status) ? "bg-blue-500 text-white" : "bg-gray-200"}">
            QUALIFIED
        </a>

        <a href="/leads?status=CONVERTED"
           class="px-4 py-2 rounded ${"CONVERTED".equals(status) ? "bg-blue-500 text-white" : "bg-gray-200"}">
            CONVERTED
        </a>
    </div>

    @if(status != null)
        <p class="text-sm text-gray-600 mb-2">
            Показаны лиды со статусом: ${status}
        </p>
    @endif

    <a
        href="/leads/new"
        class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
      >
        + Добавить лида
      </a>

      <!-- Форма поиска -->
      <form method="get" action="/leads"
            class="bg-white shadow rounded-lg p-4 mb-6 flex gap-3 items-end">

        <!-- Search -->
        <div class="flex-1">
          <label class="block text-sm text-gray-600 mb-1">Поиск</label>
          <input
            type="text"
            name="search"
            value="${search != null ? search : ""}"
            class="w-full rounded border border-gray-300 px-3 py-2
                   focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <!-- Status -->
        <div>
          <label class="block text-sm text-gray-600 mb-1">Статус</label>
          <select
            name="status"
            class="rounded border border-gray-300 px-3 py-2 bg-white
                   focus:outline-none focus:ring-2 focus:ring-blue-500">

            @if(status == null || status == "")
              <option value="" selected>ВСЕ</option>
            @else
              <option value="">ВСЕ</option>
            @endif

            @if(status.equals("NEW"))
              <option value="NEW" selected>NEW</option>
            @else
              <option value="NEW">NEW</option>
            @endif

            @if(status.equals("CONVERTED"))
              <option value="CONVERTED" selected>CONVERTED</option>
            @else
              <option value="CONVERTED">CONVERTED</option>
            @endif

            @if(status.equals("QUALIFIED"))
              <option value="QUALIFIED" selected>QUALIFIED</option>
            @else
              <option value="QUALIFIED">QUALIFIED</option>
            @endif

          </select>
        </div>

        <!-- Submit -->
        <button type="submit"
                class="bg-blue-600 hover:bg-blue-700 text-white
                       font-semibold px-4 py-2 rounded">
          Найти
        </button>
      </form>

      <!-- Reset -->
      <a href="/leads" class="text-sm text-gray-600 hover:underline">
        Сбросить фильтры
      </a>


    <h2 class="text-2xl font-bold mb-4">Lead List</h2>

    <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
            </tr>
        </thead>
        <tbody>
            @for(var lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${lead.email()}</td>
                    <td class="px-4 py-2">${lead.company()}</td>
                    <td class="px-4 py-2">
                        <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                            ${lead.status()}
                        </span>
                    </td>
                    <td class="px-4 py-2">
                    <a href="/leads/${lead.id().toString()}/edit"
                            class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
                            Редактировать
                    </a>
                    <form method="post" action="/leads/${lead.id().toString()}/delete" class="inline"
                          onclick="return confirm('Удалить лида ${lead.email()}?')">
                      <button type="submit" class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700">
                        Удалить
                      </button>
                    </form>
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
</div>
`)
