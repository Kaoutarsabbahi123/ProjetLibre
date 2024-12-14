import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLaboratoireComponent } from './add-laboratoire.component';

describe('AddLaboratoireComponent', () => {
  let component: AddLaboratoireComponent;
  let fixture: ComponentFixture<AddLaboratoireComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddLaboratoireComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddLaboratoireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
