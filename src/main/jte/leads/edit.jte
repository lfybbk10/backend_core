@import ru.mentee.power.crm.domain.Lead
@param Lead lead
@param java.util.List<ru.mentee.power.crm.domain.Company> companies
@param org.springframework.validation.BindingResult errors



<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Редактирование лида</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>

<body class="bg-gray-100">
  <div class="container mx-auto p-6 max-w-2xl">
    <h1 class="text-2xl font-bold mb-4">Редактирование лида</h1>

    <form method="post" action="/leads/${lead.getId().toString()}" class="bg-white p-6 rounded-lg shadow">
      <!-- Hidden field для ID -->
      <input type="hidden" name="id" value="${lead.getId().toString()}">

      <!-- Email -->
      <div class="mb-4">
        <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          value="${lead.getEmail()}"
          class="${errors != null && errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}"
          placeholder="ivan@example.com"
          required>
          @if(errors != null && errors.hasFieldErrors("email"))
                      <p class="text-red-600 text-sm mt-1">
                        ${errors.getFieldError("email").getDefaultMessage()}
                      </p>
                    @endif
      </div>

      <!-- Company -->
        <div class="mb-4">
            <label for="companyId">Company</label>
            <select id="companyId" name="companyId" required>
                <option value="">Выберите компанию</option>
                @for (ru.mentee.power.crm.domain.Company company : companies)
                    <option
                            value="${company.getId().toString()}"
                            selected="${lead.getCompany() != null && lead.getCompany().getId().equals(company.getId())}"
                    >
                        ${company.getName()}
                    </option>
                @endfor
            </select>
            @if(errors != null && errors.hasFieldErrors("company"))
                <p class="text-red-600 text-sm mt-1">
                    ${errors.getFieldError("company").getDefaultMessage()}
                </p>
            @endif
        </div>

      <!-- Status -->
      <div class="mb-6">
        <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
        <select
          id="status"
          name="status"
          class="w-full rounded border border-gray-300 px-3 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          required>

          @if(lead.getStatus().equals("NEW"))
            <option value="NEW" selected>NEW</option>
          @else
            <option value="NEW">NEW</option>
          @endif

          @if(lead.getStatus().equals("CONVERTED"))
            <option value="CONVERTED" selected>CONVERTED</option>
          @else
            <option value="CONVERTED">CONVERTED</option>
          @endif

          @if(lead.getStatus().equals("QUALIFIED"))
            <option value="QUALIFIED" selected>QUALIFIED</option>
          @else
            <option value="QUALIFIED">QUALIFIED</option>
          @endif

        </select>
      </div>


      <!-- Buttons -->
      <div class="flex items-center gap-3">
        <button
          type="submit"
          class="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded">
          Сохранить
        </button>

        <a
          href="/leads"
          class="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-4 py-2 rounded">
          Отмена
        </a>
      </div>
    </form>
  </div>
</body>
</html>
