import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtmTableComponent } from './atm-table.component';

describe('AtmTableComponent', () => {
  let component: AtmTableComponent;
  let fixture: ComponentFixture<AtmTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AtmTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AtmTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
