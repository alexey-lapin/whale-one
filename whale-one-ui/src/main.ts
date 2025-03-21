import './assets/tailwind.css'
import './assets/main.css'
import 'primeicons/primeicons.css'
import 'leaflet/dist/leaflet.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import Aura from '@primevue/themes/aura'
import { definePreset } from '@primevue/themes'
import ConfirmationService from 'primevue/confirmationservice'
import PrimeVue from 'primevue/config'
import Ripple from 'primevue/ripple'
import ToastService from 'primevue/toastservice'
import Tooltip from 'primevue/tooltip'

import App from './App.vue'
import router from './router'

const preset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{cyan.50}',
      100: '{cyan.100}',
      200: '{cyan.200}',
      300: '{cyan.300}',
      400: '{cyan.400}',
      500: '{cyan.500}',
      600: '{cyan.600}',
      700: '{cyan.700}',
      800: '{cyan.800}',
      900: '{cyan.900}',
      950: '{cyan.950}',
    },
  },
  components: {
    inputtext: {
      extend: {
        disabled: {
          background: '{surface.100}',
        },
      },
    },
    autocomplete: {
      extend: {
        disabled: {
          background: '{surface.100}',
        },
      },
    },
    pcinputtext: {
      extend: {
        disabled: {
          background: '{surface.100}',
        },
      },
    },
    select: {
      extend: {
        disabled: {
          background: '{surface.100}',
        },
      },
    },
    textarea: {
      extend: {
        disabled: {
          background: '{surface.100}',
        },
      },
    },
  },
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(PrimeVue, {
  ripple: true,
  theme: {
    preset: preset,
  },
})
app.directive('ripple', Ripple)
app.directive('tooltip', Tooltip);
app.use(ToastService)
app.use(ConfirmationService)

app.mount('#app')
