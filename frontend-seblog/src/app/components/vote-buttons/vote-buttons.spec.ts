import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoteButtons } from './vote-buttons';

describe('VoteButtons', () => {
  let component: VoteButtons;
  let fixture: ComponentFixture<VoteButtons>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoteButtons]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VoteButtons);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
