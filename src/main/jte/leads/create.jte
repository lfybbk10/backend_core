@param ru.mentee.power.crm.domain.Lead lead
@param java.util.List<ru.mentee.power.crm.domain.Company> companies
@param org.springframework.validation.BindingResult errors


@template.layout.main(
  content = @`
    <div class="max-w-md mx-auto mt-8">
      <h1 class="text-2xl font-bold mb-6">Добавить нового лида</h1>

      <form action="/leads" method="post" class="space-y-4">
        <!-- Email -->
        <div class="mb-4">
          <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            type="email"
            id="email"
            name="email"
            required
            class="${errors != null && errors.hasFieldErrors("email") ? "border-red-500" : "border-gray-300"}"
            placeholder="example@company.com"
          />
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
                      <option value="${company.getId().toString()}">
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


        <select name="status">
          <option value="NEW">NEW</option>
          <option value="QUALIFIED">QUALIFIED</option>
          <option value="CONVERTED">CONVERTED</option>
        </select>

        <!-- Submit Button -->
        <button
          type="submit"
          class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
        >
          Создать лида
        </button>

        <!-- Cancel Link -->
        <a
          href="/leads"
          class="block text-center text-sm text-gray-600 hover:text-gray-900"
        >
          Отмена
        </a>
      </form>
    </div>
  `
)