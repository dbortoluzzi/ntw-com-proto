import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CoreModule } from './core/core.module';
import { AuthGuard } from './core/interceptors/auth.guard.service';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'app',
    pathMatch: 'full'
  },
  { path: 'login', loadChildren: () => import('./main/login/login.module').then(m => m.LoginModule) },
  { path: 'app', loadChildren: () => import('./main/container/container.module').then(m => m.ContainerModule), canActivate: [AuthGuard] }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false, useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
