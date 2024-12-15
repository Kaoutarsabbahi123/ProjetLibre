import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoLaboratoireComponent } from './info-laboratoire.component';

describe('InfoLaboratoireComponent', () => {
  let component: InfoLaboratoireComponent;
  let fixture: ComponentFixture<InfoLaboratoireComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InfoLaboratoireComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfoLaboratoireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
