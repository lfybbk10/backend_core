@import ru.mentee.power.crm.domain.Lead
@param Lead lead

<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>Конвертация лида в сделку</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-8">
  <div class="max-w-xl mx-auto">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">Конвертировать лид в сделку</h1>
      <a href="/leads" class="text-blue-600 hover:underline">← к лидам</a>
    </div>

    <div class="bg-white p-6 rounded-lg shadow mb-4">
      <h2 class="font-semibold mb-2">Лид</h2>
      <div class="text-sm text-gray-700 space-y-1">
        <p><span class="text-gray-500">ID:</span> ${lead.id().toString()}</p>
        @if(lead.email() != null)
          <p><span class="text-gray-500">Email:</span> ${lead.email()}</p>
        @endif
        @if(lead.company() != null)
          <p><span class="text-gray-500">Компания:</span> ${lead.company()}</p>
        @endif
        @if(lead.status() != null)
          <p><span class="text-gray-500">Статус:</span> ${lead.status()}</p>
        @endif
      </div>
    </div>

    <form method="post" action="/deals/convert" class="bg-white p-6 rounded-lg shadow">
      <input type="hidden" name="leadId" value="${lead.id().toString()}">

      <label class="block text-sm font-medium text-gray-700 mb-1" for="amount">
        Сумма сделки
      </label>
      <input
        id="amount"
        name="amount"
        type="number"
        step="0.01"
        min="0"
        required
        class="w-full border rounded px-3 py-2 mb-4"
        placeholder="Например: 150000.00"
      >

      <div class="flex gap-3">
        <button type="submit" class="bg-green-600 text-white px-4 py-2 rounded">
          Конвертировать
        </button>
        <a href="/deals" class="bg-gray-200 px-4 py-2 rounded">
          Отмена
        </a>
      </div>
    </form>
  </div>
</body>
</html>
