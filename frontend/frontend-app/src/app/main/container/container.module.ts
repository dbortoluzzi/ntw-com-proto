import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CoreModule } from 'src/app/core/core.module';
import { ContainerComponent } from './container.component';

const routes: Routes = [
  {
    path: '',
    component: ContainerComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      {
        path: 'home', loadChildren: () => import('../home/home.module').then(m => m.HomeModule)
      }
    ]
  }
];
@NgModule({
  declarations: [ContainerComponent],
  imports: [RouterModule.forChild(routes), CoreModule],
  exports: [CoreModule]
})
export class ContainerModule { }
