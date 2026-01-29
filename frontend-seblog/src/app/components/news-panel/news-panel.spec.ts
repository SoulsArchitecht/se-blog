import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsPanel } from './news-panel';

describe('NewsPanel', () => {
  let component: NewsPanel;
  let fixture: ComponentFixture<NewsPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewsPanel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewsPanel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
