package open.edx.qticonverter.controllers;

import open.edx.qticonverter.models.Course;
import open.edx.qticonverter.mongomodel.Structure;
import open.edx.qticonverter.mongomodel.Version;
import open.edx.qticonverter.repositories.Structures;
import open.edx.qticonverter.repositories.Versions;
import open.edx.qticonverter.services.CourseService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping("courses")
@RestController()
public class CourseController{

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping()
    public List<Course> courses() {
        return courseService.getCourses();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable ObjectId id) {
        return courseService.getCourseById(id.toHexString());
    }
}
