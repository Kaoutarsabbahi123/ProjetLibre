import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoAnalyseComponent } from './info-analyse.component';

describe('InfoAnalyseComponent', () => {
  let component: InfoAnalyseComponent;
  let fixture: ComponentFixture<InfoAnalyseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InfoAnalyseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfoAnalyseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
