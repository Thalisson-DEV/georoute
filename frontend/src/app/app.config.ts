import { ApplicationConfig, provideBrowserGlobalErrorListeners, APP_INITIALIZER, isDevMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { inject } from '@vercel/analytics';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes), 
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),
    {
      provide: APP_INITIALIZER,
      useFactory: () => () => {
        inject({ mode: isDevMode() ? 'development' : 'production' });
      },
      multi: true
    }
  ]
};