import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TaskInstanceDetailComponent } from './task-instance-detail.component';

describe('Component Tests', () => {
  describe('TaskInstance Management Detail Component', () => {
    let comp: TaskInstanceDetailComponent;
    let fixture: ComponentFixture<TaskInstanceDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TaskInstanceDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ taskInstance: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(TaskInstanceDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TaskInstanceDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load taskInstance on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.taskInstance).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
