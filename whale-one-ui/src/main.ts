import './assets/tailwind.css'
import './assets/main.css'
import 'primeicons/primeicons.css'
import 'leaflet/dist/leaflet.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import Aura from '@primeuix/themes/aura'
import { definePreset } from '@primeuix/themes'
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
      colorScheme: {
        light: {
          root: {
            disabledBackground: '{surface.100}',
          },
        },
        dark: {
          root: {
            disabledBackground: '{surface.800}',
          },
        },
      },
    },
    autocomplete: {
      colorScheme: {
        light: {
          root: {
            disabledBackground: '{surface.100}',
          },
        },
        dark: {
          root: {
            disabledBackground: '{surface.800}',
          },
        },
      },
    },
    pcinputtext: {
      colorScheme: {
        light: {
          root: {
            disabledBackground: '{surface.100}',
          },
        },
        dark: {
          root: {
            disabledBackground: '{surface.800}',
          },
        },
      },
    },
    select: {
      colorScheme: {
        light: {
          root: {
            disabledBackground: '{surface.100}',
          },
        },
        dark: {
          root: {
            disabledBackground: '{surface.800}',
          },
        },
      },
    },
    textarea: {
      colorScheme: {
        light: {
          root: {
            disabledBackground: '{surface.100}',
          },
        },
        dark: {
          root: {
            disabledBackground: '{surface.800}',
          },
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
    options: {
      darkModeSelector: '.dark',
      cssLayer: {
        name: 'primevue',
        order: 'theme, base, primevue',
      },
    },
  },
})
app.directive('ripple', Ripple)
app.directive('tooltip', Tooltip)
app.use(ToastService)
app.use(ConfirmationService)

app.mount('#app')
