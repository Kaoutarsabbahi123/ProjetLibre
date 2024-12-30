import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoDossierComponent } from './info-dossier.component';

describe('InfoDossierComponent', () => {
  let component: InfoDossierComponent;
  let fixture: ComponentFixture<InfoDossierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InfoDossierComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfoDossierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
